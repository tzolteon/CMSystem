package org.example.controllers;

import org.example.dtos.ConferenceRequest;
import org.example.dtos.ConferenceUpdateRequest;
import org.example.dtos.PcChairRequest;
import org.example.dtos.PcMemberRequest;
import org.example.entities.Conference;
import org.example.services.ConferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/conferences")
public class ConferenceController {

    private final ConferenceService conferenceService;

    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @PostMapping("/create")
    public ResponseEntity<Conference> createConference(@RequestBody ConferenceRequest conferenceRequest) {
        Conference conference = conferenceService.createConference(
                conferenceRequest.getTitle(),
                conferenceRequest.getDescription(),
                conferenceRequest.getCreatorUsername()
        );
        return ResponseEntity.ok(conference);
    }

    @PutMapping("/{conferenceId}")
    public ResponseEntity<Conference> updateConference(@PathVariable Long conferenceId,
                                                       @RequestBody ConferenceUpdateRequest updateRequest) {
        Conference updatedConference = conferenceService.updateConference(
                conferenceId,
                updateRequest.getTitle(),
                updateRequest.getDescription(),
                updateRequest.getPcMemberIds(),
                updateRequest.getPcChairIds()
        );
        return ResponseEntity.ok(updatedConference);
    }

    @PutMapping("/{conferenceId}/pc-chairs")
    public ResponseEntity<Void> addPcChairs(@PathVariable Long conferenceId, @RequestBody PcChairRequest pcChairRequest) {
        conferenceService.addPcChairs(conferenceId, pcChairRequest.getPcChairIds());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{conferenceId}/pc-members")
    public ResponseEntity<Void> addPcMembers(@PathVariable Long conferenceId, @RequestBody PcMemberRequest pcMemberRequest) {
        conferenceService.addPcMembers(conferenceId, pcMemberRequest.getPcMemberIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Conference>> searchConferences(@RequestParam(required = false) String title,
                                                              @RequestParam(required = false) String description) {
        List<Conference> conferences = conferenceService.searchConferences(title, description);
        return ResponseEntity.ok(conferences);
    }

    @GetMapping("/{conferenceId}")
    public ResponseEntity<Conference> getConference(@PathVariable Long conferenceId) {
        Conference conference = conferenceService.getConference(conferenceId);
        return ResponseEntity.ok(conference);
    }

    @DeleteMapping("/{conferenceId}")
    public ResponseEntity<Void> deleteConference(@PathVariable Long conferenceId) {
        conferenceService.deleteConference(conferenceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{conferenceId}/start-submission")
    public ResponseEntity<Conference> startSubmission(@PathVariable Long conferenceId) {
        Conference updatedConference = conferenceService.startSubmission(conferenceId);
        return ResponseEntity.ok(updatedConference);
    }

    @PutMapping("/{conferenceId}/start-reviewer-assignment")
    public ResponseEntity<Conference> startReviewerAssignment(@PathVariable Long conferenceId) {
        Conference updatedConference = conferenceService.startReviewerAssignment(conferenceId);
        return ResponseEntity.ok(updatedConference);
    }

    @PutMapping("/{conferenceId}/start-review")
    public ResponseEntity<Conference> startReview(@PathVariable Long conferenceId) {
        Conference updatedConference = conferenceService.startReview(conferenceId);
        return ResponseEntity.ok(updatedConference);
    }

    @PutMapping("/{conferenceId}/start-decision-making")
    public ResponseEntity<Conference> startDecisionMaking(@PathVariable Long conferenceId) {
        Conference updatedConference = conferenceService.startDecisionMaking(conferenceId);
        return ResponseEntity.ok(updatedConference);
    }

    @PutMapping("/{conferenceId}/start-final-submission")
    public ResponseEntity<Conference> startFinalSubmission(@PathVariable Long conferenceId) {
        Conference updatedConference = conferenceService.startFinalSubmission(conferenceId);
        return ResponseEntity.ok(updatedConference);
    }

    @PutMapping("/{conferenceId}/end-conference")
    public ResponseEntity<Conference> endConference(@PathVariable Long conferenceId) {
        Conference updatedConference = conferenceService.endConference(conferenceId);
        return ResponseEntity.ok(updatedConference);
    }

}
