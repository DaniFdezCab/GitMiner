package aiss.gitminer.repository;

import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitRepository extends JpaRepository<Commit, String> {


    Page<Commit> findByTitle(String title, Pageable pageable);







}
