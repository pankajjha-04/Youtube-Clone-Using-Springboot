package com.programming.pankaj.youtubeclone.service;

import com.programming.pankaj.youtubeclone.dto.CommentDto;
import com.programming.pankaj.youtubeclone.dto.UploadVideoResponse;
import com.programming.pankaj.youtubeclone.dto.VideoDto;
import com.programming.pankaj.youtubeclone.model.Comment;
import com.programming.pankaj.youtubeclone.model.Video;
import com.programming.pankaj.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final UserService userService;

    // Method to upload a video
    public UploadVideoResponse uploadVideo(MultipartFile multipartFile) {
        // Upload the video file to Amazon S3 and get the video URL
        String videoUrl = s3Service.uploadFile(multipartFile);

        // Create a new Video object and set the video URL
        var video = new Video();
        video.setVideoUrl(videoUrl);

        // Save the video object to the database
        var savedVideo = videoRepository.save(video);

        // Return the response containing the saved video's ID and URL
        return new UploadVideoResponse(savedVideo.getId(), savedVideo.getVideoUrl());
    }

    // Method to edit a video
    public VideoDto editVideo(VideoDto videoDto) {
        // Find the video by ID
        var savedVideo = getVideoById(videoDto.getId());

        // Update the video's fields with the values from videoDto
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());

        // Save the updated video to the database
        videoRepository.save(savedVideo);

        // Return the updated videoDto
        return videoDto;
    }

    // Method to upload a thumbnail image for a video
    public String uploadThumbnail(MultipartFile file, String videoId) {
        // Find the video by ID
        var savedVideo = getVideoById(videoId);

        // Upload the thumbnail image file to Amazon S3 and get the thumbnail URL
        String thumbnailUrl = s3Service.uploadFile(file);

        // Set the thumbnail URL for the video
        savedVideo.setThumbnailUrl(thumbnailUrl);

        // Save the updated video to the database
        videoRepository.save(savedVideo);

        // Return the thumbnail URL
        return thumbnailUrl;
    }

    // Helper method to get a Video object by its ID
    private Video getVideoById(String videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by ID - " + videoId));
    }

    // Method to get the details of a video
    public VideoDto getVideoDetails(String videoId) {
        // Get the video by ID
        Video savedVideo = getVideoById(videoId);

        // Increase the view count of the video
        increaseVideoCount(savedVideo);

        // Add the video to the user's watch history
        userService.addVideoToHistory(videoId);

        // Map the Video object to VideoDto and return
        return mapToVideoDto(savedVideo);
    }

    // Helper method to increase the view count of a video
    private void increaseVideoCount(Video savedVideo) {
        savedVideo.incrementViewCount();
        videoRepository.save(savedVideo);
    }

    // Method to like a video
    public VideoDto likeVideo(String videoId) {
        // Get the video by ID
        Video videoById = getVideoById(videoId);

        // Check if the user has already liked the video
        if (userService.ifLikedVideo(videoId)) {
            // If the user has already liked the video, decrement the likes count and remove it from the liked videos list
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
        } else if (userService.ifDislikedVideos(videoId))
        {
            // If the user has already disliked the video, decrement the dislikes count, remove it from the disliked videos list,
            // increment the likes count, and add it to the liked videos list
            videoById.decrementDislikes();
            userService.removeFromDislikedVideos(videoId);
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        } else {
            // If the user has not liked or disliked the video, increment the likes count and add it to the liked videos list
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }

        // Save the updated video to the database
        videoRepository.save(videoById);

        // Map the Video object to VideoDto and return
        return mapToVideoDto(videoById);
    }

    // Method to dislike a video
    public VideoDto disLikeVideo(String videoId) {
        // Get the video by ID
        Video videoById = getVideoById(videoId);

        // Check if the user has already disliked the video
        if (userService.ifDislikedVideos(videoId)) {
            // If the user has already disliked the video, decrement the dislikes count and remove it from the disliked videos list
            videoById.decrementDislikes();
            userService.removeFromDislikedVideos(videoId);
        } else if (userService.ifLikedVideo(videoId)) {
            // If the user has already liked the video, decrement the likes count, remove it from the liked videos list,
            // increment the dislikes count, and add it to the disliked videos list
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            videoById.incrementDislikes();
            userService.addToDislikedVideos(videoId);
        } else {
            // If the user has not liked or disliked the video, increment the dislikes count and add it to the disliked videos list
            videoById.incrementDislikes();
            userService.addToDislikedVideos(videoId);
        }

        // Save the updated video to the database
        videoRepository.save(videoById);

        // Map the Video object to VideoDto and return
        return mapToVideoDto(videoById);
    }

    // Helper method to map a Video object to VideoDto
    private VideoDto mapToVideoDto(Video videoById) {
        VideoDto videoDto = new VideoDto();
        videoDto.setVideoUrl(videoById.getVideoUrl());
        videoDto.setThumbnailUrl(videoById.getThumbnailUrl());
        videoDto.setId(videoById.getId());
        videoDto.setTitle(videoById.getTitle());
        videoDto.setDescription(videoById.getDescription());
        videoDto.setTags(videoById.getTags());
        videoDto.setVideoStatus(videoById.getVideoStatus());
        videoDto.setLikeCount(videoById.getLikes().get());
        videoDto.setDislikeCount(videoById.getDisLikes().get());
        videoDto.setViewCount(videoById.getViewCount().get());
        return videoDto;
    }

    // Method to add a comment to a video
    public void addComment(String videoId, CommentDto commentDto) {
        // Get the video by ID
        Video video = getVideoById(videoId);

        // Create a new Comment object with the comment text and author ID
        Comment comment = new Comment();
        comment.setText(commentDto.getCommentText());
        comment.setAuthorId(commentDto.getAuthorId());

        // Add the comment to the video's comment list
        video.addComment(comment);

        // Save the updated video to the database
        videoRepository.save(video);
    }

    // Method to get all comments for a video
    public List<CommentDto> getAllComments(String videoId) {
        // Get the video by ID
        Video video = getVideoById(videoId);

        // Get the list of comments for the video
        List<Comment> commentList = video.getCommentList();

        // Map the list of comments to CommentDto objects and return
        return commentList.stream().map(this::mapToCommentDto).toList();
    }

    // Helper method to map a Comment object to CommentDto
    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentText(comment.getText());
        commentDto.setAuthorId(comment.getAuthorId());
        return commentDto;
    }

    // Method to get all videos
    public List<VideoDto> getAllVideos() {
        // Get all videos from the database
        return videoRepository.findAll().stream().map(this::mapToVideoDto).toList();
    }



    }


