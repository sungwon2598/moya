//package com.study.moya.admin.controller;
//
//import org.springframework.web.bind.annotation.*;
//
//import java.awt.print.Pageable;
//
//@RestController
//@RequestMapping("/api/admin/posts")
//public class AdminPostController {
//    private final AdminPostService adminPostService;
//
//    @GetMapping
//    public Page<AdminPostResponse> getPosts(Pageable pageable) {
//        return adminPostService.getPosts(pageable);
//    }
//
//    @GetMapping("/{postId}")
//    public AdminPostDetailResponse getPostDetail(@PathVariable Long postId) {
//        return adminPostService.getPostDetail(postId);
//    }
//
//    @DeleteMapping("/{postId}")
//    public void deletePost(@PathVariable Long postId) {
//        adminPostService.deletePost(postId);
//    }
//}