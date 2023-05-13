package aiss.gitminer.controller;

import aiss.gitminer.model.Project;
import aiss.gitminer.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.net.HttpURLConnection;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    RestTemplate restTemplate;

    private ProjectRepository repository;
    public ProjectController(){
    }

    @ResponseStatus(HttpStatus.CREATED)

    @PostMapping("/{id}")
    public Project createGitHubProject(@Valid @RequestBody Project project) {
        return repository.create(project);
    }


    @GetMapping("/{id}")
    public Project postProjectGitLab(@PathVariable String id,
                               @RequestParam(required = false, name = "sinceCommits") Integer sinceCommits,
                               @RequestParam(required = false, name = "sinceIssues") Integer sinceIssues,
                               @RequestParam(required = false, name = "maxPages") Integer maxPages){
        if(sinceCommits==null){
            sinceCommits=2;
        }if(sinceIssues==null) {
            sinceIssues=20;
        }if(maxPages==null){
            maxPages=2;
        }
        Project project = null;

        if(repository.exists()){
            project = repository.getProject(id);
        }else{
            project = restTemplate.getForObject("http://localhost:8082/api/project/" +  owner + "/" + repo
                    + "?sinceCommits=" + sinceCommits + "&sinceIssues="
                    + sinceIssues +"&maxPages=" + maxPages, Project.class);
        }

        return project;
    }

    @GetMapping("/{owner}/{repo}")
    public Project postProjectGitHub(@PathVariable String owner,
                                     @PathVariable String repo,
                               @RequestParam(required = false, name = "sinceCommits") Integer sinceCommits,
                               @RequestParam(required = false, name = "sinceIssues") Integer sinceIssues,
                               @RequestParam(required = false, name = "maxPages") Integer maxPages){
        if(sinceCommits==null){
            sinceCommits=2;
        }if(sinceIssues==null) {
            sinceIssues=20;
        }if(maxPages==null){
            maxPages=2;
        }
        Project project = null;

        if(repository.exists()){
            project = repository.getProject(id);
        }else{
            project = restTemplate.getForObject("http://localhost:8082/api/project/" +  owner + "/" + repo
                    + "?sinceCommits=" + sinceCommits + "&sinceIssues="
                    + sinceIssues +"&maxPages=" + maxPages, Project.class);

        }

        return project;
    }

}
