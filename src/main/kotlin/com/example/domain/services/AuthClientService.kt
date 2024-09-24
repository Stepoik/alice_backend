package com.example.domain.services

import com.example.api.dtos.ClientCredentials
import com.example.api.dtos.UserCredentials
import com.example.api.requests.UserRegistrationRequest
import com.example.domain.exceptions.AuthCodeIncorrect
import com.example.domain.exceptions.ClientNotExistException
import com.example.domain.exceptions.SecretIncorrect
import com.example.domain.exceptions.UserCredentialsIncorrect
import com.example.domain.models.AuthClient
import com.example.domain.models.AuthCode
import com.example.domain.models.CodeWithRedirectURI
import com.example.domain.models.User
import com.example.domain.repositories.AuthClientRepository
import com.example.domain.repositories.AuthCodeRepository
import com.example.domain.repositories.UserRepository
import com.example.core.AuthCodeGenerator
import com.example.core.jwt.JwtConfigurer
import com.example.core.PasswordEncoder
import com.example.core.UserIdGenerator

class AuthClientService(
    private val repository: AuthClientRepository,
    private val userRepository: UserRepository,
    private val authCodeRepository: AuthCodeRepository,
    private val jwtConfigurer: JwtConfigurer
) {

    suspend fun isClientIdExist(clientId: String): Boolean {
        return repository.getClientById(clientId) != null
    }

    suspend fun getAuthCode(clientId: String, credentials: UserCredentials): Result<CodeWithRedirectURI> {
        val authClient = repository.getClientById(clientId) ?: return Result.failure(ClientNotExistException())
        val user = getUserWithCorrectCredentials(credentials) ?: return Result.failure(UserCredentialsIncorrect())
        return Result.success(saveAuthCode(clientId = clientId, userId = user.id, redirectUri = authClient.redirectUri))
    }

    suspend fun getRegisterAuthCode(
        clientId: String,
        credentials: UserRegistrationRequest
    ): Result<CodeWithRedirectURI> {
        if (credentials.login.length < 8) {
            return Result.failure(Exception("Login minimum length is 8"))
        }
        if (credentials.username.length < 8) {
            return Result.failure(Exception("Name minimum length is 8"))
        }
        val authClient = repository.getClientById(clientId) ?: return Result.failure(ClientNotExistException())
        return runCatching {
            userRepository.createUser(credentials.toUser(UserIdGenerator.generate()))
        }.map { user ->
            saveAuthCode(clientId = clientId, userId = user.id, redirectUri = authClient.redirectUri)
        }
    }

    suspend fun authorizeUser(clientId: String, secret: String, code: String): Result<String> {
        if (!isClientIdExist(clientId)) {
            return Result.failure(ClientNotExistException())
        }
        if (!isSecretCorrect(clientId = clientId, secret = secret)) {
            return Result.failure(SecretIncorrect())
        }
        val authCode = authCodeRepository.get(clientId, code) ?: return Result.failure(AuthCodeIncorrect())
        authCodeRepository.remove(clientId, code)
        val user = userRepository.getUserById(authCode.userId) ?: return Result.failure(UserCredentialsIncorrect())
        val jwtCode = jwtConfigurer.generateToken(userId = user.id, userName = user.name)
        return Result.success(jwtCode)
    }

    suspend fun createClient(clientCredentials: ClientCredentials) {
        repository.saveClient(client = authClientFromCredentials(clientCredentials))
    }

    private suspend fun saveAuthCode(clientId: String, userId: String, redirectUri: String): CodeWithRedirectURI {
        val code = AuthCodeGenerator.generate()
        val authCode = AuthCode(code = code, clientId = clientId, userId = userId)
        authCodeRepository.add(authCode)
        return CodeWithRedirectURI(code = code, redirectUri = redirectUri)
    }

    private suspend fun isSecretCorrect(clientId: String, secret: String): Boolean {
        val authClient = repository.getClientById(clientId)
        return authClient?.let {
            it.secret == secret
        } ?: false
    }

    private suspend fun getUserWithCorrectCredentials(credentials: UserCredentials): User? {
        val user = userRepository.getUserByLogin(credentials.login)
        return user?.let {
            if (it.password == PasswordEncoder.encode(credentials.password)) it else null
        }
    }
}

private fun UserRegistrationRequest.toUser(id: String) = User(
    id = id,
    name = username,
    password = PasswordEncoder.encode(password),
    login = login
)

private fun authClientFromCredentials(credentials: ClientCredentials) = AuthClient(
    clientId = credentials.clientId,
    name = credentials.name,
    secret = credentials.secret,
    redirectUri = credentials.redirectUri
)