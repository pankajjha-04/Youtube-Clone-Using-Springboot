package com.programming.pankaj.youtubeclone.repository;

import com.programming.pankaj.youtubeclone.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video, String> {
}

