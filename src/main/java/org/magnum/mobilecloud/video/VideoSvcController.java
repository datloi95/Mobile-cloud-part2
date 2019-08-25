/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import com.google.common.collect.Lists;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;



@Controller
public class VideoSvcController {
	
	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "VideoSvcController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */

	@Autowired
	private VideoRepository videos;
	
	@RequestMapping(value="/go",method=RequestMethod.GET)
	public @ResponseBody String goodLuck(){
		return "Good Luck!";
	}


	@RequestMapping(value = "/video", method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList(){

		return Lists.newArrayList(videos.findAll());
	}

	@RequestMapping(value = "/video", method = RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v){

		videos.save(v);
		return v;
	}

	@RequestMapping(value = "/video/{id}", method = RequestMethod.GET)
	public @ResponseBody Video getVideo(@PathVariable("id") long id, HttpServletResponse response) throws IOException {

		Video video = videos.findOne(id);
		if (video!=null){
			return video;
		}
		response.setStatus(404);
		return null;
	}

	@RequestMapping(value = "/video/{id}/like", method = RequestMethod.POST)
	public @ResponseBody void likeVideo(@PathVariable("id") long id, Principal p, HttpServletResponse response) throws IOException {

		Video video = videos.findOne(id);
		if (video==null){
			response.setStatus(400);
			return;
		}

		String username = p.getName();
		Set<String> userLike = video.getLikedBy();
		long numLike = video.getLikes();

		if (userLike.contains(username)){
			response.setStatus(400);
			return;
		}

		userLike.add(username);
		numLike++;
		video.setLikedBy(userLike);
		video.setLikes(numLike);
		videos.save(video);

		response.setStatus(200);
		return;

	}

	@RequestMapping(value = "/video/{id}/unlike", method = RequestMethod.POST)
	public @ResponseBody void unlikeVideo(@PathVariable("id") long id, Principal p, HttpServletResponse response) throws IOException {

		Video video = videos.findOne(id);
		if (video==null){
			response.setStatus(400);
			return;
		}

		String username = p.getName();
		Set<String> userLike = video.getLikedBy();
		long numLike = video.getLikes();

		if (!userLike.contains(username)){
			response.setStatus(400);
			return;
		}

		userLike.remove(username);
		numLike--;
		video.setLikedBy(userLike);
		video.setLikes(numLike);
		videos.save(video);

		response.setStatus(200);
		return;
	}


	@RequestMapping(value = "/video/search/findByName?title={title}", method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(@RequestParam("title") String title) {

		return videos.findByName(title);
	}

	@RequestMapping(value = "/video/search/findByDurationLessThan?duration={duration}", method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(@RequestParam("duration") long duration) {

		return videos.findByDurationLessThan(duration);
	}


	
}
