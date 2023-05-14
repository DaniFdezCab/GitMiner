package aiss.gitminer.controller;


import aiss.gitminer.exceptions.ProjectNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import aiss.gitminer.repository.CommitRepository;
import aiss.gitminer.repository.ProjectRepository;
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
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;

@Tag(name = "Project", description = "Project management API")
@RestController
@RequestMapping("/gitminer/projects")


public class ProjectController {


    @Autowired
    ProjectRepository repository;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of projects",
                    content = { @Content(schema = @Schema(implementation = Project.class), mediaType = "application/json") })
    })

    @Operation(
            summary = "Retrieve a list of projects",
            description = "Get a list of projects",
            tags = { "get" })
    @GetMapping
    public List<Project> findAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String name,
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

        Page<Project> projectPage;
        if(name == null){
            projectPage = repository.findAll(paging);
        }else {
            projectPage = repository.findByName(name, paging);
        }


        return projectPage.getContent();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Correctly retrieved",
                    content = { @Content(schema = @Schema(implementation = Project.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description="Project not found", content = { @Content(schema = @Schema()) }) })

    @Operation(
            summary = "Retrieve a Project by Id",
            description = "Get a Project object by specifying its id",
            tags = { "get" })
    @GetMapping("/{id}")

    public Project findOne(@Parameter(description = "id of Project to be searched ") @PathVariable String id) throws ProjectNotFoundException{
        Optional<Project> project = repository.findById(id);
        if(!project.isPresent()){
            throw new ProjectNotFoundException();
        }
        return project.get();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Correctly created",
                    content = { @Content(schema = @Schema(implementation = Project.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description="Bad Request",
                    content = { @Content(schema = @Schema()) }) })

    @Operation(
            summary = "Create a Project",
            description = "Post a Projects object",
            tags = { "post" })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Project createProject(@Valid @RequestBody  Project project){
        Project project1 = repository.save(new Project(project.getName(),project.getWebUrl(),project.getCommits(), project.getIssues()));
        return project1;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content",
                    content = { @Content(schema = @Schema(implementation = Project.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description="Bad Request",
                    content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description="Project not found",
                    content = { @Content(schema = @Schema()) }) })
    @Operation(
            summary = "Update a Project",
            description = "Updates a Project object",
            tags = { "put" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Project updateProject, @Parameter(description = "id of the Project to be updated") @PathVariable String id)throws ProjectNotFoundException{
        Optional<Project> projectData = repository.findById(id);

        if(!projectData.isPresent()){
            throw new ProjectNotFoundException();
        }

        Project _project = projectData.get();
        _project.setName(updateProject.getName());
        _project.setWebUrl(updateProject.getWebUrl());
        _project.setCommits(updateProject.getCommits());
        _project.setIssues(updateProject.getIssues());
        repository.save(_project);

    }




    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content",
                    content = { @Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description="Bad Request",
                    content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description="Project not found",
                    content = { @Content(schema = @Schema()) }) })

    @Operation(
            summary = "Delete a Project",
            description = "Delete a Project by an id",
            tags = { "delete" })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete( @Parameter(description = "id of the Project to be deleted") @PathVariable String id)throws ProjectNotFoundException{
        if(repository.existsById(id)){
            repository.deleteById(id);
        }

        else{
            throw new ProjectNotFoundException();
        }

    }












}
