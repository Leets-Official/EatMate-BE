package com.example.eatmate.app.domain.image.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.image.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
