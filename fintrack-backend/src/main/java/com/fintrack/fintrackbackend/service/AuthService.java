package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.LoginRequest;
import com.fintrack.fintrackbackend.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
