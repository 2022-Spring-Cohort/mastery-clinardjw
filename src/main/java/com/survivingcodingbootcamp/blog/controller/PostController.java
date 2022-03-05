package com.survivingcodingbootcamp.blog.controller;

import com.survivingcodingbootcamp.blog.model.Hashtag;
import com.survivingcodingbootcamp.blog.model.Post;
import com.survivingcodingbootcamp.blog.model.Topic;
import com.survivingcodingbootcamp.blog.repository.HashtagRepository;
import com.survivingcodingbootcamp.blog.repository.PostRepository;
import com.survivingcodingbootcamp.blog.repository.TopicRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {
    private PostRepository postRepo;
    private HashtagRepository hashtagRepo;
    private TopicRepository topicRepo;

    public PostController(PostRepository postRepo, HashtagRepository hashtagRepo, TopicRepository topicRepo) {
        this.postRepo = postRepo;
        this.hashtagRepo = hashtagRepo;
        this.topicRepo = topicRepo;
    }


    @GetMapping("/{id}")
    public String displaySinglePost(@PathVariable long id, Model model) {
        model.addAttribute("post", postRepo.findById(id).get());
        return "single-post-template";
    }


    @PostMapping("/post/{id}/addhashtag")
    public String addHashtag(@PathVariable long id, @RequestParam String hashtag){
        Post myPost = postRepo.findById(id).get();
        Optional<Hashtag> optHashtag = hashtagRepo.findByHashtag(hashtag);

        if (optHashtag.isPresent() && myPost.getHashtags().contains(optHashtag.get())) {
            return "redirect:/post/"+ id;
        } else if (optHashtag.isPresent()) {
            myPost.addHashtag(optHashtag.get());
            postRepo.save(myPost);
        } else {
            Hashtag hashtag1 = new Hashtag(hashtag);
            hashtagRepo.save(hashtag1);
            myPost.addHashtag(hashtag1);
            postRepo.save(myPost);
        }

        return "redirect:/post/"+ id;
    }

    @PostMapping("/{id}/addnewpost")
    public String addNewPost(@PathVariable long id, @RequestParam String title, @RequestParam String author, @RequestParam String content) {
        Optional<Topic> tempTopic = topicRepo.findById(id);
        Optional<Post> newPost = postRepo.findByTitle(title);

        if (tempTopic.isPresent()) {
            Post tempPost;
            if (newPost.isPresent()) {
                tempPost = newPost.get();
            } else {
                tempPost = new Post(title, tempTopic.get(), content, author);
            }
            postRepo.save(tempPost);
        }
        return "redirect:/topics/" + id;

    }

}
