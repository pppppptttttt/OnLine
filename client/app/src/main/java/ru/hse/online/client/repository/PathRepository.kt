package ru.hse.online.client.repository

import ru.hse.online.client.repository.networking.api_data.PathRequest
import ru.hse.online.client.repository.networking.api_data.PathResult
import ru.hse.online.client.repository.networking.api_service.PathApiService
import java.util.UUID

class PathRepository(private val pathApiService: PathApiService) {

    suspend fun getPaths(token: String, userId: UUID): PathResult {
        return try {
            val response = pathApiService.getPaths(token, userId)
            when (response.code()) {
                200 -> PathResult.Success(
                    paths = response.body(),
                    code = response.code()
                )
                else -> PathResult.Failure(
                    code = response.code(),
                    message = response.message()
                )
            }
        } catch (e: Exception) {
            PathResult.Failure(message = e.localizedMessage)
        }
    }

    suspend fun createPath(token: String, path: PathRequest): PathResult {
        return try {
            val response = pathApiService.createPath(token, path)
            when (response.code()) {
                204 -> PathResult.Success(code = response.code())
                else -> PathResult.Failure(
                    code = response.code(),
                    message = response.message()
                )
            }
        } catch (e: Exception) {
            PathResult.Failure(message = e.localizedMessage)
        }
    }

    suspend fun deletePath(token: String, userId: UUID, pathId: UUID): PathResult {
        return try {
            val response = pathApiService.deletePath(token, userId, pathId)
            when (response.code()) {
                204 -> PathResult.Success(code = response.code())
                else -> PathResult.Failure(
                    code = response.code(),
                    message = response.message()
                )
            }
        } catch (e: Exception) {
            PathResult.Failure(message = e.localizedMessage)
        }
    }
}