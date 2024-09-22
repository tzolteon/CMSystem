package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.CoAuthorRequest;
import org.example.dtos.FinalSubmissionRequest;
import org.example.dtos.PaperRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PaperControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2aXNpdG9yIiwiaWF0IjoxNzI3MDE0NDQ3LCJleHAiOjE3MjcwNTA0NDd9.LALC16JWfziWvx2Vsq4InANsh_Pk8iFu-fjdGkoWBrk";

    @Test
    public void testCreatePaper() throws Exception {
        PaperRequest paperRequest = new PaperRequest();
        paperRequest.setTitle("Test paper 2445");
        paperRequest.setRequestorUsername("visitor");
        paperRequest.setAbstractText("An abstract of the paper");
        paperRequest.setContent("Main content of the paper");
        paperRequest.setConferenceId(1L);

        mockMvc.perform(post("/api/papers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paperRequest))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated());

    }

    @Test
    public void testUpdatePaper() throws Exception {
        PaperRequest PaperRequest = new PaperRequest();
        PaperRequest.setTitle("Updated Paper Title222222");
        PaperRequest.setAbstractText("Updated abstract of the paper");
        PaperRequest.setContent("Updated main content of the paper");

        mockMvc.perform(put("/api/papers/{paperId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(PaperRequest))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddCoAuthor() throws Exception {

        CoAuthorRequest coAuthorRequest = new CoAuthorRequest();
        coAuthorRequest.setCoAuthorUsername("pc-chair");

        mockMvc.perform(put("/api/papers//{paperId}/coauthor", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coAuthorRequest))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testSubmitPaper() throws Exception {
        PaperRequest PaperRequest = new PaperRequest();
        PaperRequest.setContent("Final content of the paper with addressed comments");

        mockMvc.perform(post("/api/papers/{id}/submit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(PaperRequest))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testAssignReviewer() throws Exception {

        String reviewer = new String("reviewer");
        mockMvc.perform(put("/api/papers/{paperId}/assign-reviewer", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewer)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testApprovePaper() throws Exception {
        mockMvc.perform(put("/api/papers/{paperId}/approve", 1L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testRejectPaper() throws Exception {
        mockMvc.perform(put("/api/papers/{paperId}/reject", 1L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("REJECTED"));
    }

    @Test
    public void testFinalSubmitPaper() throws Exception {
        FinalSubmissionRequest request = new FinalSubmissionRequest();
        request.setUpdatedContent("Final version of the paper content.");
        request.setAddressingReviewerComments("Addressed all reviewer comments thoroughly.");

        mockMvc.perform(put("/api/papers/{paperId}/final-submit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testAcceptPaper() throws Exception {
        mockMvc.perform(put("/api/papers/{paperId}/accept", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testSearchPapers() throws Exception {
        mockMvc.perform(get("/api/papers/search")
                        .param("authorName", "author"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSearchWithoutCriteria() throws Exception {
        mockMvc.perform(get("/api/papers/search"))
                .andExpect(status().isOk());
    }

    @Test
    public void testWithdrawPaper() throws Exception {
        mockMvc.perform(delete("/api/papers/{paperId}/withdraw", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

}
