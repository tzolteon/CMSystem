package org.example.controllers;

import org.example.dtos.CoAuthorRequest;
import org.example.dtos.FinalSubmissionRequest;
import org.example.dtos.PaperRequest;
import org.example.entities.Paper;
import org.example.entities.User;
import org.example.security.JwtService;
import org.example.services.PaperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    private final PaperService paperService;
    private final JwtService jwtUtil;

    public PaperController(PaperService paperService, JwtService jwtUtil) {
        this.paperService = paperService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Paper createPaper(@RequestBody PaperRequest paperRequest) {
        return paperService.createPaper(
                paperRequest.getTitle(),
                paperRequest.getAbstractText(),
                paperRequest.getContent(),
                paperRequest.getConferenceId(),
                paperRequest.getRequestorUsername()
        );
    }

    @PutMapping("/{paperId}")
    public Paper updatePaper(@PathVariable Long paperId, @RequestBody PaperRequest paperRequest) {
        return paperService.updatePaper(paperId, paperRequest.getTitle(), paperRequest.getAbstractText(), paperRequest.getContent());
    }

    @PutMapping("/{paperId}/coauthor")
    @ResponseStatus(HttpStatus.OK)
    public Paper addCoAuthor(@PathVariable Long paperId, @RequestBody CoAuthorRequest coAuthorRequest) {
        return paperService.addCoAuthor(paperId, coAuthorRequest.getCoAuthorUsername());
    }

    @PostMapping("/{paperId}/submit")
    @ResponseStatus(HttpStatus.OK)
    public Paper submitPaper(@PathVariable Long paperId) {
        return paperService.submitPaper(paperId);
    }



    @PutMapping("/{paperId}/assign-reviewer")
    public ResponseEntity<Paper> assignReviewer(@PathVariable Long paperId, @RequestBody String reviewerUsername) {
        Paper updatedPaper = paperService.assignReviewer(paperId, reviewerUsername);
        return new ResponseEntity<>(updatedPaper, HttpStatus.OK);
    }

    @PutMapping("/{paperId}/approve")
    public ResponseEntity<?> approvePaper(@PathVariable Long paperId) {
        try {
            Paper approvedPaper = paperService.approvePaper(paperId);
            return ResponseEntity.ok(approvedPaper);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{paperId}/reject")
    public ResponseEntity<?> rejectPaper(@PathVariable Long paperId) {
        try {
            Paper rejectedPaper = paperService.rejectPaper(paperId);
            return ResponseEntity.ok(rejectedPaper);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{paperId}/final-submit")
    public ResponseEntity<?> finalSubmitPaper(@PathVariable Long paperId,
                                              @RequestBody FinalSubmissionRequest request) {
        try {
            Paper submittedPaper = paperService.finalSubmitPaper(
                    paperId, request.getUpdatedContent(), request.getAddressingReviewerComments());
            return ResponseEntity.ok(submittedPaper);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{paperId}/accept")
    public ResponseEntity<?> acceptPaper(@PathVariable Long paperId) {
        try {
            Paper acceptedPaper = paperService.acceptPaper(paperId);
            return ResponseEntity.ok(acceptedPaper);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Paper>> searchPapers(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) String abstractText) {

        List<Paper> papers = paperService.searchPapers(title, authorName, abstractText);
        return ResponseEntity.ok(papers);
    }

    @DeleteMapping("/{paperId}/withdraw")
    public ResponseEntity<Void> withdrawPaper(@PathVariable Long paperId,
                                              @RequestHeader("Authorization") String token) {
        // Assuming token can be used to extract user details
        // Extract the user from the token
        User currentUser = new User();
        currentUser.setUsername("reviewer");
        //currentUser.setUsername(jwtUtil.getUsernameFromToken(token));

        // Withdraw the paper (deletes it)
        paperService.withdrawPaper(paperId, currentUser);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content on successful delete
    }
}
