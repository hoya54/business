package mpti.domain.opinion.dao;

import mpti.domain.opinion.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByWriterId(Long writerId);

    Optional<Review> findById(Long id);

    List<Review> findAllByTrainerId(Long trainerId);
}