package com.programming.pankaj.youtubeclone.controller; // Package declaration

import com.programming.pankaj.youtubeclone.dto.CommentDto;
import com.programming.pankaj.youtubeclone.dto.UploadVideoResponse;
import com.programming.pankaj.youtubeclone.dto.VideoDto;
import com.programming.pankaj.youtubeclone.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController // Indicates that this class is a REST controller
@RequestMapping("/api/videos") // Base path for all the endpoints in this controller
@RequiredArgsConstructor // Generates a constructor for initializing the final fields
public class VideoController {

    private final VideoService videoService; // VideoService dependency injected through constructor

    @PostMapping // Handles HTTP POST requests to the base path ("/api/videos")
    @ResponseStatus(HttpStatus.CREATED) // Sets the HTTP response status to 201 (Created)
    public UploadVideoResponse uploadVideo(@RequestParam("file") MultipartFile file) {
        // Method for uploading a video file, takes a MultipartFile as input
        return videoService.uploadVideo(file); // Delegate the task to the VideoService and return the response
    }

    @PostMapping("/thumbnail") // Handles HTTP POST requests to "/api/videos/thumbnail"
    @ResponseStatus(HttpStatus.CREATED) // Sets the HTTP response status to 201 (Created)
    public String uploadThumbnail(@RequestParam("file") MultipartFile file, @RequestParam("videoId") String videoId) {
        // Method for uploading a video thumbnail, takes a MultipartFile and a videoId as input
        return videoService.uploadThumbnail(file, videoId); // Delegate the task to the VideoService and return the response
    }

    @PutMapping // Handles HTTP PUT requests to the base path ("/api/videos")
    @ResponseStatus(HttpStatus.OK) // Sets the HTTP response status to 200 (OK)
    public VideoDto editVideoMetadata(@RequestBody VideoDto videoDto) {
        // Method for editing video metadata, takes a VideoDto object as input in the request body
        return videoService.editVideo(videoDto); // Delegate the task to the VideoService and return the updated video metadata
    }

    @GetMapping("/{videoId}") // Handles HTTP GET requests to "/api/videos/{videoId}"
    @ResponseStatus(HttpStatus.OK) // Sets the HTTP response status to 200 (OK)
    public VideoDto getVideoDetails(@PathVariable String videoId) {
        // Method for retrieving video details, takes a videoId as input from the path variable
        return videoService.getVideoDetails(videoId); // Delegate the task to the VideoService and return the video details
    }

    @PostMapping("/{videoId}/like") // Handles HTTP POST requests to "/api/videos/{videoId}/like"
    @ResponseStatus(HttpStatus.OK) // Sets the HTTP response status to 200 (OK)
    public VideoDto likeVideo(@PathVariable String videoId) {
        // Method for liking a video, takes a videoId as input from the path variable
        return videoService.likeVideo(videoId); // Delegate the task to the VideoService and return the updated video details
    }

    @PostMapping("/{videoId}/disLike") // Handles HTTP POST requests to "/api/videos/{videoId}/disLike"
    @ResponseStatus(HttpStatus.OK) // Sets the HTTP response status to 200 (OK)
    public VideoDto disLikeVideo(@PathVariable String videoId) {
        // Method for disliking a video, takes a videoId as input from the path variable
        return videoService.disLikeVideo(videoId); // Delegate the task to the VideoService and return the updated video details
    }

    @PostMapping("/{videoId}/comment") // Handles HTTP POST requests to "/api/videos/{videoId}/comment"
    @ResponseStatus(HttpStatus.OK) // Sets the HTTP response status to 200 (OK)
    public void addComment(@PathVariable String videoId, @RequestBody CommentDto commentDto) {
        // Method for adding a comment to a video, takes a videoId from the path variable and a CommentDto object from the request body
        videoService.addComment(videoId, commentDto); // Delegate the task to the VideoService to add the comment
    }

    @GetMapping("/{videoId}/comment") // Handles HTTP GET requests to "/api/videos/{videoId}/comment"
    @ResponseStatus(HttpStatus.OK) // Sets the HTTP response status to 200 (OK)
    public List<CommentDto> getAllComments(@PathVariable String videoId) {
        // Method for retrieving all comments for a video, takes a videoId as input from the path variable
        return videoService.getAllComments(videoId); // Delegate the task to the VideoService and return the list of comments
    }

    @GetMapping // Handles HTTP GET requests to the base path ("/api/videos")
    @ResponseStatus(HttpStatus.OK) // Sets the HTTP response status to 200 (OK)
    public List<VideoDto> getAllVideos() {
        // Method for retrieving all videos
        return videoService.getAllVideos(); // Delegate the task to the VideoService and return the list of videos
    }
}
