package aiss.gitminer.repository;

import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, String> {


    Page<Comment> findByAuthor(String author, Pageable pageable);












}
