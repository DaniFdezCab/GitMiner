package aiss.gitminer.controller;


import aiss.gitminer.exceptions.CommentNotFoundException;
import aiss.gitminer.exceptions.ProjectNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import aiss.gitminer.repository.CommentRepository;
import aiss.gitminer.repository.IssueRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name = "Comment", description = "Comment management API")
@RestController
@RequestMapping("/gitminer")
public class CommentController {

    @Autowired
    CommentRepository repository;

    @Autowired
    IssueRepository issueRepository;




    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of comments",
                    content = { @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json") })
    })

    @Operation(
            summary = "Retrieve a list of comments",
            description = "Get a list of comments",
            tags = { "get" })
    @GetMapping("/comments")
    public List<Comment> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String order
    ){

        Pageable paging;
        if(order != null) {
            if (order.startsWith("-"))
                paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
            else
                paging = PageRequest.of(page, size, Sort.by(order).ascending());
        }
        else
            paging = PageRequest.of(page, size);

        Page<Comment> commentPage;
        if(author == null){
            commentPage = repository.findAll(paging);
        }else {
            commentPage = repository.findByAuthor(author, paging);
        }


        return commentPage.getContent();
    }



    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Correctly retrieved",
                    content = { @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description="Comment not found", content = { @Content(schema = @Schema()) }) })
    @Operation(
            summary = "Retrieve a Comment by id",
            description = "Get a Comment object by specifying its id",
            tags = { "get" })
    @GetMapping("/comments/{id}")
    public Comment findOne(@Parameter(description = "id of Comment to be searched")  @PathVariable String id) throws CommentNotFoundException{


        Optional<Comment> comment = repository.findById(id);
        if(!comment.isPresent()){
            throw new CommentNotFoundException();
        }
        return comment.get();
    }




    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Correctly retrieved",
                    content = { @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description="Issue not found", content = { @Content(schema = @Schema()) }) })
    @Operation(
            summary = "Retrieve a Comment by Issue id",
            description = "Get a list of Comment objects by specifying an Issue id",
            tags = { "get" })
    @GetMapping("/issues/{id}/comments")
    public List<Comment> findCommentByIssueId(@Parameter(description = "id of an Issue")  @PathVariable String id){
        Optional<Issue> issue = issueRepository.findById(id);
        return issue.get().getComments();
    }


    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content",
                    content = { @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description="Bad Request",
                    content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description="Comment not found",
                    content = { @Content(schema = @Schema()) }) })
    @Operation(
            summary = "Update a Comment ",
            description = "Updates a Comment object",
            tags = { "put" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/comments/{id}")
    public void update(@Valid @RequestBody Comment updateComment, @Parameter(description = "id of the Comment to be updated") @PathVariable String id)throws CommentNotFoundException{
        Optional<Comment> commentData = repository.findById(id);

        if(!commentData.isPresent()){
            throw new CommentNotFoundException();
        }
        Comment comment = commentData.get();
        comment.setId(updateComment.getId());
        comment.setBody(updateComment.getBody());
        comment.setAuthor(updateComment.getAuthor());
        comment.setCreatedAt(updateComment.getCreatedAt());
        comment.setUpdatedAt(updateComment.getUpdatedAt());
        repository.save(comment);

    }





}
