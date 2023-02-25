package sg.edu.nus.iss.workshop27.model;

import java.util.Date;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @NotNull
    @Pattern(regexp = "[a-zA-Z]+", message = "Only alphabets allowed.")
    private String user;

    @NotNull(message = "Please input a rating.")
    @Positive(message = "Please input a rating.") // html prevents value to go negative
    @Min(value = 1, message = "Minimum rating is 1.") // html limits value to 1
    @Max(value = 10, message = "Maximum rating is 10.") // html limits value to 10
    private Integer rating;

    private String comment;

    @NotNull(message = "Please input game id")
    @Positive(message = "Please input a rating.")
    private Integer gid;

    @PastOrPresent
    private Date posted;

    private String name; // name of the game as per ID
}
