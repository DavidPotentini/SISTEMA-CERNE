package com.github.davidpotentini.repository.papeis;
import com.github.davidpotentini.model.papeis.PapeisModel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PapeisRepository extends JpaRepository<PapeisModel, Long> {
}
