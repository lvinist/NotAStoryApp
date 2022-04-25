package com.alph.storyapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.alph.storyapp.api.ApiService
import com.alph.storyapp.data.Story
import com.alph.storyapp.utils.Constant.NETWORK_LOAD_SIZE
import com.alph.storyapp.utils.Constant.STORY_API_STARTING_INDEX
import retrofit2.HttpException
import java.io.IOException

class StoryPagingSource(private val apiService: ApiService, private val token: String) : PagingSource<Int, Story>() {

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: STORY_API_STARTING_INDEX
            val response = apiService.getStories(
                page = position,
                size = NETWORK_LOAD_SIZE,
                token = token)

            val stories = response.listStory
            val nextKey = if (stories.isNullOrEmpty()) null else position + 1
            LoadResult.Page(
                data = stories.orEmpty(),
                prevKey = if (position == STORY_API_STARTING_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}