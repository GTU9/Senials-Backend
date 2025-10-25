package com.senials.suggestion.repository;

import com.senials.suggestion.entity.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestionRepository extends JpaRepository<Suggestion,Integer> {

}
