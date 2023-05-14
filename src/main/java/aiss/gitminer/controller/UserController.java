package aiss.gitminer.controller;


import aiss.gitminer.exceptions.ProjectNotFoundException;
import aiss.gitminer.exceptions.UserNotFoundException;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import aiss.gitminer.model.User;
import aiss.gitminer.repository.UserRepository;
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

@Tag(name = "User", description = "User management API")
@RestController
@RequestMapping("/gitminer/users")
public class UserController {

    @Autowired
    UserRepository repository;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of Users",
                    content = { @Content(schema = @Schema(implementation = User.class), mediaType = "application/json") })
    })
    @Operation(
            summary = "Retrieve a list of users",
            description = "Get a list of users",
            tags = { "get" })
    @GetMapping
    public List<User> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
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

        Page<User> userPage;
        if(name == null){
            userPage = repository.findAll(paging);
        }else {
            userPage = repository.findByName(name, paging);
        }







        return userPage.getContent();
    }



    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Correctly retrieved",
                    content = { @Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description="User not found", content = { @Content(schema = @Schema()) }) })
    @Operation(
            summary = "Retrieve an User by Id",
            description = "Get an User object by specifying its id",
            tags = { "get" })
    @GetMapping("/{id}")
    public User findOne(@Parameter(description = "id of User to be searched ") @PathVariable String id) throws UserNotFoundException {
        Optional<User> user = repository.findById(id);
        if(!user.isPresent()){
            throw new UserNotFoundException();
        }
        return user.get();
    }



    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Correctly created",
                    content = { @Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description="Bad Request",
                    content = { @Content(schema = @Schema()) }) })

    @Operation(
            summary = "Create an User",
            description = "Post an User object",
            tags = { "post" })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@Valid @RequestBody  User user){
        User user1 = repository.save(new User(user.getId(),user.getUsername(),user.getName(),user.getAvatarUrl(), user.getWebUrl()));
        return user1;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content",
                    content = { @Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description="Bad Request",
                    content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description="User not found",
                    content = { @Content(schema = @Schema()) }) })
    @Operation(
            summary = "Update an User",
            description = "Updates an User object",
            tags = { "put" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody User updatedUser, @Parameter(description = "id of the User to be updated") @PathVariable String id)throws UserNotFoundException{
        Optional<User> userData = repository.findById(id);

        if(!userData.isPresent()){
            throw new UserNotFoundException();
        }
        User user = userData.get();
        user.setId(updatedUser.getId());
        user.setUsername(updatedUser.getUsername());
        user.setName(updatedUser.getName());
        user.setAvatarUrl(updatedUser.getAvatarUrl());
        user.setWebUrl(updatedUser.getWebUrl());
        repository.save(user);

    }




    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content",
                    content = { @Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description="Bad Request",
                    content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description="User not found",
                    content = { @Content(schema = @Schema()) }) })

    @Operation(
            summary = "Delete an User",
            description = "Delete an User by an id",
            tags = { "delete" })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete( @Parameter(description = "id of the User to be deleted") @PathVariable String id)throws UserNotFoundException{
        if(repository.existsById(id)){
            repository.deleteById(id);
        }
        else{
            throw new UserNotFoundException();
        }

    }






}
