package org.example.services;

import org.example.dtos.PaperRequest;
import org.example.entities.*;
import org.example.enums.ConferenceState;
import org.example.enums.PaperState;
import org.example.repositories.PaperRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.ConferenceRepository;
import org.example.repositories.UserRoleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class PaperService {

    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ConferenceRepository conferenceRepository;

    private static final int MAX_REVIEWERS = 2;

    public PaperService(PaperRepository paperRepository, UserRepository userRepository, UserRoleRepository userRoleRepository, ConferenceRepository conferenceRepository) {
        this.paperRepository = paperRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.conferenceRepository = conferenceRepository;
    }

    public Paper createPaper(String title, String abstractText, String content, Long conferenceId, String requestorUsername) {
        if (paperRepository.existsByTitle(title)) {
            throw new RuntimeException("Paper title must be unique.");
        }

        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        User requestor = userRepository.findByUsername(requestorUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Paper paper = new Paper();
        paper.setTitle(title);
        paper.setAbstractText(abstractText);
        paper.setContent(content);
        paper.setCreationDate(LocalDateTime.now());
        paper.setConference(conference);
        paper.setAuthors(Collections.singletonList(requestor));
        paper.setState(PaperState.PENDING);

        boolean hasAuthorRole = requestor.getRoles().stream()
                .anyMatch(role -> role.getRole().equals("AUTHOR"));

        if (!hasAuthorRole) {
            UserRole authorRole = new UserRole();
            authorRole.setRole("AUTHOR");
            authorRole.setUser(requestor);

            userRoleRepository.save(authorRole);

            requestor.getRoles().add(authorRole);
            userRepository.save(requestor);
        }


        User author = userRepository.findByUsername(requestorUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        paperRepository.save(paper);

        return paper;
        }

    public Paper submitPaper(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        if (paper.getContent() == null || paper.getContent().isEmpty()) {
            throw new RuntimeException("Paper content cannot be empty for submission.");
        }

        Conference conference = paper.getConference();
        if (conference == null || conference.getState() != ConferenceState.SUBMISSION) {
            throw new RuntimeException("The conference is not in a submission state.");
        }

        paper.setState(PaperState.SUBMITTED);

        return paperRepository.save(paper);
    }

    public Paper addCoAuthor(Long paperId, String coAuthorUsername) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        User coAuthor = userRepository.findByUsername(coAuthorUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!paper.getAuthors().contains(coAuthor)) {
            paper.getAuthors().add(coAuthor);

            UserRole authorRole = new UserRole();
            authorRole.setRole("AUTHOR");

            authorRole.setUser(coAuthor);

            coAuthor.getRoles().add(authorRole);

            userRepository.save(coAuthor);
        }

        return paperRepository.save(paper);
    }

    public Paper assignReviewer(Long paperId, String reviewerUsername) {
        // Find the paper by ID
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        Conference conference = paper.getConference();
        if (conference == null || conference.getState() != ConferenceState.ASSIGNMENT) {
            throw new RuntimeException("The conference is not in the ASSIGNMENT state.");
        }

        User reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        boolean isPcMember = conference.getPcMembers().contains(reviewer);
        if (!isPcMember) {
            throw new RuntimeException("Reviewer is not a member of the Programme Committee.");
        }

        if (paper.getReviewers().size() >= MAX_REVIEWERS) {
            throw new RuntimeException("The paper already has the maximum number of reviewers.");
        }

        paper.getReviewers().add(reviewer);

        return paperRepository.save(paper);
    }


    public Paper updatePaper(Long paperId, String title, String abstractText, String content) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        if (!paper.getTitle().equals(title) && paperRepository.existsByTitle(title)) {
            throw new RuntimeException("Paper title must be unique.");
        }

        paper.setTitle(title);
        paper.setAbstractText(abstractText);
        paper.setContent(content);

        return paperRepository.save(paper);
    }

    public Paper reviewPaper(Long paperId, String reviewerUsername, int score, String justification) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        User reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        if (!paper.getConference().getPcMembers().contains(reviewer)) {
            throw new RuntimeException("Only PC members can review papers.");
        }

        if (paper.getState() != PaperState.SUBMITTED && paper.getState() != PaperState.UNDER_REVIEW) {
            throw new RuntimeException("Paper must be submitted and under review.");
        }

        boolean hasReviewed = paper.getReviews().stream()
                .anyMatch(review -> review.getReviewer().equals(reviewer));

        if (hasReviewed) {
            throw new RuntimeException("Reviewer has already submitted a review for this paper.");
        }

        Review review = new Review();
        review.setPaper(paper);
        review.setReviewer(reviewer);
        review.setScore(score);
        review.setJustification(justification);

        paper.getReviews().add(review);

        paper.setState(PaperState.UNDER_REVIEW);

        return paperRepository.save(paper);
    }

    public Paper viewPaper(Long paperId, User requestor) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        if (paper.getAuthors().contains(requestor) || paper.getConference().getPcChairs().contains(requestor)) {
            return paper;
        } else {
            Paper limitedViewPaper = new Paper();
            limitedViewPaper.setTitle(paper.getTitle());
            limitedViewPaper.setAbstractText(paper.getAbstractText());
            return limitedViewPaper;
        }
    }

    public Paper approvePaper(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        Conference conference = paper.getConference();

        if (conference.getState() != ConferenceState.DECISION) {
            throw new IllegalStateException("Paper can only be approved during the DECISION phase.");
        }

        // Update paper state to 'APPROVED' but not yet accepted
        paper.setState(PaperState.APPROVED_PENDING_MODIFICATIONS);

        return paperRepository.save(paper);
    }

    public Paper rejectPaper(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        Conference conference = paper.getConference();

        if (conference.getState() != ConferenceState.DECISION) {
            throw new IllegalStateException("Paper can only be rejected during the DECISION phase.");
        }

        paper.setState(PaperState.REJECTED);

        return paperRepository.save(paper);
    }

    public Paper finalSubmitPaper(Long paperId, String updatedContent, String addressingReviewerComments) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        Conference conference = paper.getConference();

        if (conference.getState() != ConferenceState.FINAL_SUBMISSION) {
            throw new IllegalStateException("Final submission is only allowed during the FINAL_SUBMISSION phase.");
        }

        if (updatedContent == null || updatedContent.trim().isEmpty()) {
            throw new IllegalArgumentException("The paper content cannot be empty for final submission.");
        }

        paper.setContent(updatedContent);
        paper.setReviewComments(addressingReviewerComments);

        return paperRepository.save(paper);
    }

    public Paper acceptPaper(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        Conference conference = paper.getConference();

        if (conference.getState() != ConferenceState.FINAL) {
            throw new IllegalStateException("Paper acceptance is only allowed during the FINAL phase of the conference.");
        }
        paper.setState(PaperState.ACCEPTED);

        return paperRepository.save(paper);
    }

    public List<Paper> searchPapers(String title, String authorName, String abstractText) {
        if (isSearchCriteriaEmpty(title, authorName, abstractText)) {
            return paperRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
        } else {
            return paperRepository.searchByCriteria(title, authorName, abstractText);
        }
    }

    private boolean isSearchCriteriaEmpty(String title, String authorName, String abstractText) {
        return (title == null || title.isBlank()) &&
                (authorName == null || authorName.isBlank()) &&
                (abstractText == null || abstractText.isBlank());
    }

    public void withdrawPaper(Long paperId, User currentUser) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        // Only allow withdrawal if the current user is an author or PC Chair

        Conference conference = paper.getConference();
        boolean isPcMember = conference.getPcMembers().contains(currentUser);

        if (!paper.getAuthors().contains(currentUser) && !isPcMember) {
            throw new RuntimeException("User not authorized to withdraw this paper");
        }

        // Remove the paper
        paperRepository.delete(paper);
    }

}
