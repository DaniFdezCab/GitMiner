package aiss.gitminer.controller;


import aiss.gitminer.exceptions.CommitNotFoundException;
import aiss.gitminer.model.Commit;
import aiss.gitminer.repository.CommitRepository;
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

@Tag(name = "Commit", description = "Commit management API")
@RestController
@RequestMapping("/gitminer")
public class CommitController {
    @Autowired
    CommitRepository repository;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of commits",
                    content = { @Content(schema = @Schema(implementation = Commit.class), mediaType = "application/json") })
    })
    @Operation(
            summary = "Retrieve a list of commits",
            description = "Get a list of commits",
            tags = { "get" })
    @GetMapping("/commits")

    public List<Commit> findAll(@Parameter(description = "Email that it´s used to filter") @RequestParam(value = "email", required = false) String email,
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

        Page<Commit> commitPage;
        if(title == null){
            commitPage = repository.findAll(paging);
        }else {
            commitPage = repository.findByTitle(title, paging);
        }


        if(email != null) {
            return commitPage.stream().filter(x -> x.getAuthorEmail().equals(email)).toList();
        }else {
            return commitPage.getContent();
        }
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Correctly retrieved",
                    content = { @Content(schema = @Schema(implementation = Commit.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description="Commit not found", content = { @Content(schema = @Schema()) }) })
    @Operation(
            summary = "Retrieve a Commit by Id",
            description = "Get a Commit object by specifying its id",
            tags = { "get" })
    @GetMapping("/commits/{id}")
    public Commit findOne( @Parameter(description = "id of Commit to be searched ") @PathVariable String id) throws CommitNotFoundException{
        Optional<Commit> commit = repository.findById(id);
        if(!commit.isPresent()){
            throw new CommitNotFoundException();
        }
        return commit.get();
    }
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content",
                    content = { @Content(schema = @Schema(implementation = Commit.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description="Bad Request",
                    content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description="Commit not found",
                    content = { @Content(schema = @Schema()) }) })
    @Operation(
            summary = "Update a commit ",
            description = "Updates a commit object",
            tags = { "put" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/commits/{id}")
    public void update(@Valid @RequestBody Commit updatedCommit, @Parameter(description = "id of the Commit to be updated") @PathVariable String id)throws CommitNotFoundException{
        Optional<Commit> commitData = repository.findById(id);

        if(!commitData.isPresent()){
            throw new CommitNotFoundException();
        }

        Commit commit = commitData.get();
        commit.setTitle(updatedCommit.getTitle());
        commit.setMessage(updatedCommit.getMessage());
        commit.setAuthorName(updatedCommit.getAuthorName());
        commit.setAuthorEmail(updatedCommit.getAuthorEmail());
        commit.setAuthoredDate(updatedCommit.getAuthoredDate());
        commit.setCommitterName(updatedCommit.getCommitterName());
        commit.setCommitterEmail(updatedCommit.getCommitterEmail());
        commit.setCommittedDate(updatedCommit.getCommittedDate());
        commit.setWebUrl(updatedCommit.getWebUrl());

        repository.save(commit);

    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content",
                    content = { @Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description="Bad Request",
                    content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description="Commit not found",
                    content = { @Content(schema = @Schema()) }) })

    @Operation(
            summary = "Delete a Commit",
            description = "Delete a Commit by an id",
            tags = { "delete" })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/commits/{id}")
    public void delete( @Parameter(description = "id of the Commit to be deleted") @PathVariable String id)throws CommitNotFoundException {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        }
        else {
            throw new CommitNotFoundException();
        }


    }








}
