package fyp.sudoku.sudoku_api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info = @Info(
        title = "Sudoku API",
        version = "1.0",
        description = "API documentation for generating and solving Sudoku puzzles"
    )
)

@SpringBootApplication
public class SudokuApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SudokuApiApplication.class, args);
  }

}

