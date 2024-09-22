package org.example.services;

import org.example.entities.Conference;
import org.example.entities.Paper;
import org.example.entities.User;
import org.example.entities.UserRole;
import org.example.enums.ConferenceState;
import org.example.enums.PaperState;
import org.example.repositories.ConferenceRepository;
import org.example.repositories.PaperRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConferenceService {

    private final ConferenceRepository conferenceRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PaperRepository paperRepository;

    public ConferenceService(ConferenceRepository conferenceRepository, UserRepository userRepository, UserRoleRepository userRoleRepository, PaperRepository paperRepository) {
        this.conferenceRepository = conferenceRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.paperRepository = paperRepository;
    }

    public Conference createConference(String name, String description, String creatorUsername) {

        if (conferenceRepository.existsByTitle(name)) {
            throw new RuntimeException("Conference name must be unique");
        }

        Conference conference = new Conference();
        conference.setTitle(name);
        conference.setDescription(description);
        conference.setCreationDate(LocalDateTime.now());
        conference.setState(ConferenceState.CREATED);


        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        conference.getPcChairs().add(creator);

        UserRole pcChairRole = new UserRole();
        pcChairRole.setRole("PC_CHAIR");
        pcChairRole.setUser(creator);
        creator.getRoles().add(pcChairRole);

        Conference savedConference = conferenceRepository.save(conference);
        return savedConference;
    }


    public Conference updateConference(Long conferenceId, String newName, String newDescription, List<Long> newPcMemberIds, List<Long> newPcChairIds) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        if (newName != null && !newName.isEmpty()) {
            conference.setTitle(newName);
        }

        if (newDescription != null && !newDescription.isEmpty()) {
            conference.setDescription(newDescription);
        }

        List<User> newPcMembers = userRepository.findAllById(newPcMemberIds);
        conference.setPcMembers(newPcMembers);

        User creator = conference.getPcChairs().get(0); // Assuming creator is the first chair
        List<User> newPcChairs = userRepository.findAllById(newPcChairIds);

        if (!newPcChairs.contains(creator)) {
            newPcChairs.add(0, creator);
        }
        conference.setPcChairs(newPcChairs);

        return conferenceRepository.save(conference);
    }

    public void addPcChairs(Long conferenceId, List<Long> pcChairIds) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        for (Long pcChairId : pcChairIds) {
            User user = userRepository.findById(pcChairId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!conference.getPcChairs().contains(user)) {
                conference.getPcChairs().add(user);
            }
        }

        conferenceRepository.save(conference);
    }

    public void addPcMembers(Long conferenceId, List<Long> pcMemberIds) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        for (Long pcMemberId : pcMemberIds) {
            User user = userRepository.findById(pcMemberId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!conference.getPcMembers().contains(user)) {
                conference.getPcMembers().add(user);
            }
        }

        conferenceRepository.save(conference);
    }

    public List<Conference> searchConferences(String title, String description) {
        if ((title == null || title.isEmpty()) && (description == null || description.isEmpty())) {
            return conferenceRepository.findAll();
        }

        return conferenceRepository.findByTitleContainingAndDescriptionContaining(title, description)
                .stream()
                .sorted((c1, c2) -> c1.getTitle().compareToIgnoreCase(c2.getTitle()))
                .collect(Collectors.toList());
    }

    public Conference getConference(Long conferenceId) {
        return conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
    }

    public Conference viewConference(Long conferenceId, User requestor) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        if (conference.getPcChairs().contains(requestor) || conference.getPcMembers().contains(requestor)) {
            return conference;
        } else {
            Conference limitedViewConference = new Conference();
            limitedViewConference.setTitle(conference.getTitle());
            limitedViewConference.setDescription(conference.getDescription());
            return limitedViewConference;
        }
    }


    public void deleteConference(Long conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        conferenceRepository.delete(conference);
    }

    public Conference startSubmission(Long conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        if (conference.getState() != ConferenceState.CREATED) {
            throw new IllegalStateException("Submission can only be started from CREATED state.");
        }

        conference.setState(ConferenceState.SUBMISSION);
        return conferenceRepository.save(conference);
    }

    public Conference startReviewerAssignment(Long conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        if (conference.getState() != ConferenceState.SUBMISSION) {
            throw new IllegalStateException("Reviewer assignment can only be started from SUBMISSION state.");
        }

        conference.setState(ConferenceState.ASSIGNMENT);
        return conferenceRepository.save(conference);
    }

    public Conference startReview(Long conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        if (conference.getState() != ConferenceState.ASSIGNMENT) {
            throw new IllegalStateException("Review can only be started from ASSIGNMENT state.");
        }

        conference.setState(ConferenceState.REVIEW);
        return conferenceRepository.save(conference);
    }

    public Conference startDecisionMaking(Long conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        if (conference.getState() != ConferenceState.REVIEW) {
            throw new IllegalStateException("Decision making can only be started from REVIEW state.");
        }

        conference.setState(ConferenceState.DECISION);
        return conferenceRepository.save(conference);
    }

    public Conference startFinalSubmission(Long conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        if (conference.getState() != ConferenceState.DECISION) {
            throw new IllegalStateException("Final submission can only be started from DECISION state.");
        }

        conference.setState(ConferenceState.FINAL_SUBMISSION);
        return conferenceRepository.save(conference);
    }

    public Conference endConference(Long conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));

        if (conference.getState() != ConferenceState.FINAL_SUBMISSION) {
            throw new IllegalStateException("Conference can only be ended from FINAL_SUBMISSION state.");
        }

        conference.setState(ConferenceState.FINAL);
        Conference updatedConference = conferenceRepository.save(conference);

        List<Paper> papers = paperRepository.findByConferenceId(conferenceId);

        for (Paper paper : papers) {
            if (paper.getState() == PaperState.APPROVED_PENDING_MODIFICATIONS) {
                if (paper.getContent() != null && !paper.getContent().isEmpty()) {
                    paper.setState(PaperState.ACCEPTED);
                } else {
                    paper.setState(PaperState.REJECTED);
                }
                paperRepository.save(paper);
            }
        }

        return updatedConference;
    }
}
