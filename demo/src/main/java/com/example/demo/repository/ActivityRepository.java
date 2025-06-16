package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findTop5ByOrderByCreatedAtDesc();
}
