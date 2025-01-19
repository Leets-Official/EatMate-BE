package com.example.eatmate.app.domain.block.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.block.domain.Block;

public interface BlockRepository extends JpaRepository<Block, Long> {
}
