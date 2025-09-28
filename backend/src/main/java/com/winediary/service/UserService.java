package com.winediary.service;

import com.winediary.dto.UserDto;
import com.winediary.entity.User;
import com.winediary.repository.TastingNoteRepository;
import com.winediary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final TastingNoteRepository tastingNoteRepository;

    @Transactional
    public User createUser(UserDto.Request.SignUp request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .provider(request.getProvider())
                .providerId(request.getProviderId())
                .build();

        return userRepository.save(user);
    }

    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public UserDto.Response.Profile getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long totalNotes = tastingNoteRepository.countByUserId(userId);
        Double averageRating = tastingNoteRepository.findAverageRatingByUserId(userId);

        return UserDto.Response.Profile.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .totalNotes(totalNotes)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .build();
    }

    public UserDto convertToDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }
}