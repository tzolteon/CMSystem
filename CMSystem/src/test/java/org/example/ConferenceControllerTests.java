package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.ConferenceUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ConferenceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2aXNpdG9yIiwiaWF0IjoxNzI3MDE0NDQ3LCJleHAiOjE3MjcwNTA0NDd9.LALC16JWfziWvx2Vsq4InANsh_Pk8iFu-fjdGkoWBrk";

    @Test
    public void testCreateConference() throws Exception {
        Map<String, Object> conferenceRequest = new HashMap<>();
        conferenceRequest.put("title", "Test 12341234");
        conferenceRequest.put("description", "A description for the test conference");
        conferenceRequest.put("creatorUsername", "pc-chair");

        mockMvc.perform(post("/api/conferences/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conferenceRequest))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateConference() throws Exception {
        ConferenceUpdateRequest updateRequest = new ConferenceUpdateRequest();
        updateRequest.setTitle("Updated Conference Title");
        updateRequest.setDescription("Updated conference description");
        updateRequest.setPcMemberIds(Collections.singletonList(2L));
        updateRequest.setPcChairIds(Collections.singletonList(3L));

        mockMvc.perform(put("/api/conferences/{conferenceId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetConference() throws Exception {
        mockMvc.perform(get("/api/conferences/{conferenceId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteConference() throws Exception {
        mockMvc.perform(delete("/api/conferences/{conferenceId}", 1L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testAddPcChairs() throws Exception {
        Map<String, Object> pcChairRequest = new HashMap<>();
        pcChairRequest.put("pcChairIds", Collections.singletonList(2L));

        mockMvc.perform(put("/api/conferences/{conferenceId}/pc-chairs", 9L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pcChairRequest))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddPcMembers() throws Exception {
        Map<String, Object> pcMemberRequest = new HashMap<>();
        pcMemberRequest.put("pcMemberIds", Collections.singletonList(3L));

        mockMvc.perform(put("/api/conferences/{conferenceId}/pc-members", 9L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pcMemberRequest))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testSearchConferences() throws Exception {
        mockMvc.perform(get("/api/conferences/search")
                        .param("title", "Test Conference")
                        .param("description", "A test description")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testStartSubmission() throws Exception {
        mockMvc.perform(put("/api/conferences/{conferenceId}/start-submission", 10L)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testStartReviewerAssignment() throws Exception {
        mockMvc.perform(put("/api/conferences/{conferenceId}/start-reviewer-assignment", 10L)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testStartReview() throws Exception {
        mockMvc.perform(put("/api/conferences/{conferenceId}/start-review", 10L)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testStartDecisionMaking() throws Exception {
        mockMvc.perform(put("/api/conferences/{conferenceId}/start-decision-making", 10L)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testStartFinalSubmission() throws Exception {
        mockMvc.perform(put("/api/conferences/{conferenceId}/start-final-submission", 10L)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testEndConference() throws Exception {
        mockMvc.perform(put("/api/conferences/{conferenceId}/end-conference", 10L)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
