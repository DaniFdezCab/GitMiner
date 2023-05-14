package aiss.gitminer.controller;


import aiss.gitminer.exceptions.IssueNotFoundException;
import aiss.gitminer.model.Issue;
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

@Tag(name = "Issue", description = "Issue management API")
@RestController
@RequestMapping("/gitminer")
public class IssueController {


    @Autowired
    IssueRepository repository;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of issues",
                    content = { @Content(schema = @Schema(implementation = Issue.class), mediaType = "application/json") })
    })
    @Operation(
            summary = "Retrieve a list of issues",
            description = "Get a list of issues",
            tags = { "get" })
    @GetMapping("/issues")
    public List<Issue> findAll(
            @Parameter(description = "author id that it´s used to be searched") @RequestParam(value = "authorId", required = false) String authorId,
            @Parameter(description = "state that it´s used to filter ") @RequestParam(value = "state", required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String order){

        Pageable paging;
        if(order != null) {
            if (order.startsWith("-"))
                paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
            else
                paging = PageRequest.of(page, size, Sort.by(order).ascending());
        }
        else
            paging = PageRequest.of(page, size);

        Page<Issue> issuePage;
        if(title == null){
            issuePage = repository.findAll(paging);
        }else {
            issuePage = repository.findByTitle(title, paging);
        }



        List<Issue> res = issuePage.getContent();

        if(authorId != null) {
            res =  res.stream().filter(x -> x.getAuthor().getId().equals(authorId)).toList();
        }
        if(state != null){
            res = res.stream().filter(x -> x.getState().equals(state)).toList();
        }
        return res;
    }




    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Correctly retrieved",
                    content = { @Content(schema = @Schema(implementation = Issue.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description="Issue not found", content = { @Content(schema = @Schema()) }) })
    @Operation(
            summary = "Retrieve an Issue by Id",
            description = "Get an Issue object by specifying its id",
            tags = { "get" })
    @GetMapping("/issues/{id}")
    public Issue findOneByid(@Parameter(description = "id of Issue to be searched") @PathVariable String id) throws IssueNotFoundException{
        Optional<Issue> issue = repository.findById(id);
        if(!issue.isPresent()){
            throw new IssueNotFoundException();
        }
        return issue.get();
    }


    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content",
                    content = { @Content(schema = @Schema(implementation = Issue.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description="Bad Request",
                    content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description="Issue not found",
                    content = { @Content(schema = @Schema()) }) })
    @Operation(
            summary = "Update an Issue ",
            description = "Updates an Issue object",
            tags = { "put" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/issues/{id}")
    public void update(@Valid @RequestBody Issue updateIssue, @Parameter(description = "id of the Issue to be updated") @PathVariable String id) throws IssueNotFoundException{
        Optional<Issue> issueData = repository.findById(id);

        if(!issueData.isPresent()){
            throw new IssueNotFoundException();
        }
        Issue issue = issueData.get();
        issue.setId(updateIssue.getId());
        issue.setRefId(updateIssue.getRefId());
        issue.setTitle(updateIssue.getTitle());
        issue.setDescription(updateIssue.getDescription());
        issue.setState(updateIssue.getState());
        issue.setCreatedAt(updateIssue.getCreatedAt());
        issue.setUpdatedAt(updateIssue.getUpdatedAt());
        issue.setClosedAt(updateIssue.getClosedAt());
        issue.setLabels(updateIssue.getLabels());
        issue.setAuthor(updateIssue.getAuthor());
        issue.setAssignee(updateIssue.getAssignee());
        issue.setUpvotes(updateIssue.getUpvotes());
        issue.setDownvotes(updateIssue.getDownvotes());
        issue.setWebUrl(updateIssue.getWebUrl());
        issue.setComments(updateIssue.getComments());
        repository.save(issue);

    }

}
