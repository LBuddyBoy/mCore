package dev.minechase.core.rest.service;

import dev.minechase.core.rest.jwt.JwtUtil;
import dev.minechase.core.rest.model.Profile;
import dev.minechase.core.rest.repository.ProfileRepository;
import dev.minechase.core.rest.util.HashUtil;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private ProfileRepository profileRepository;
    private MongoTemplate mongoTemplate;
    private JwtUtil jwtUtil;

    public ProfileService(ProfileRepository profileRepository, MongoTemplate mongoTemplate, JwtUtil jwtUtil) {
        this.profileRepository = profileRepository;
        this.mongoTemplate = mongoTemplate;
        this.jwtUtil = jwtUtil;
    }

    public List<Profile> getProfiles() {
        return this.profileRepository.findAll();
    }

    public Page<Profile> getProfiles(int pageNumber, int pageSize) {
        return this.profileRepository.findAll(Pageable.ofSize(pageSize).withPage(pageNumber));
    }

    public Profile insertProfile(Profile profile) {
        return this.profileRepository.insert(profile);
    }

    public Optional<Profile> findById(String stringId) {
        if (!ObjectId.isValid(stringId)) return Optional.empty();

        return this.profileRepository.findById(new ObjectId(stringId));
    }

    public Optional<Profile> findById(ObjectId id) {
        return this.profileRepository.findById(id);
    }

    public Optional<Profile> findByUsername(String username) {
        return this.profileRepository.findByUsername(username);
    }

    public Optional<Profile> findByEmail(String email) {
        return this.profileRepository.findByEmail(email);
    }
    
    public Optional<Profile> findByToken(String token) {
        return this.profileRepository.findByToken(token);
    }
    
    public Optional<Profile> findByEmailVerifyToken(String emailVerifyToken) {
        return this.profileRepository.findByEmailVerifyToken(emailVerifyToken);
    }

    public String createToken(Profile profile) {
        String token = this.jwtUtil.createToken(profile.getId().toHexString());

        profile.setToken(token);
        this.profileRepository.save(profile);

        return token;
    }

    public void saveProfile(Profile profile) {
        profile.setPassword(HashUtil.encryptUsingKey(profile.getPassword()));

        this.profileRepository.save(profile);
    }


}
