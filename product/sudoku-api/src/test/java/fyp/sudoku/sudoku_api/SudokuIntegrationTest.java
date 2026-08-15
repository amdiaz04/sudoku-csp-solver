package fyp.sudoku.sudoku_api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


/**
 * Integration test that verifies the full flow from controller through to the solver library using
 * a real application context. It verifies generate returns correct grid and difficulty verifies
 * solve returns grid and performance metrics.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SudokuApiApplication.class})
@WebAppConfiguration
public class SudokuIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  public void testGenerateAndSolve() throws Exception {
    mockMvc.perform(get("/api/sudoku/generate/EASY"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.grid").isArray())
        .andExpect(jsonPath("$.difficulty").value("EASY"));

    mockMvc.perform(post("/api/sudoku/solve"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.grid").isArray())
        .andExpect(jsonPath("$.backtracks").isNumber())
        .andExpect(jsonPath("$.nodesVisited").isNumber());
  }
}