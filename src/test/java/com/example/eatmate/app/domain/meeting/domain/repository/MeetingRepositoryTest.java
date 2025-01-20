package com.example.eatmate.app.domain.meeting.domain.repository;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.image.domain.Image;
import com.example.eatmate.app.domain.image.domain.ImageType;
import com.example.eatmate.app.domain.image.domain.repository.ImageRepository;
import com.example.eatmate.app.domain.meeting.domain.GenderRestriction;
import com.example.eatmate.app.domain.meeting.domain.Meeting;
import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
class MeetingRepositoryTest {

	@Autowired
	private OfflineMeetingRepository offlineMeetingRepository;

	@Autowired
	private ImageRepository imageRepository;

	@PersistenceContext
	private EntityManager entityManager;  // 추가

	@Test
	@Transactional
	void test() {
		// Given
		Image image = imageRepository.save(
			Image.builder().imageUrl("imageUrl").type(ImageType.MEETING_BACKGROUND).build());

		Meeting meeting = offlineMeetingRepository.save(OfflineMeeting.builder()
			.meetingName("title")
			.meetingDescription("description")
			.backgroundImage(image)
			.genderRestriction(GenderRestriction.MALE)
			.offlineMeetingCategory(OfflineMeetingCategory.BEVERAGE)
			.meetingStatus(MeetingStatus.INACTIVE)
			.meetingDate(LocalDateTime.of(2025, 10, 16, 0, 0))
			.meetingPlace("place")
			.build());

		entityManager.flush();
		entityManager.clear();

		// When

		Meeting savedMeeting = offlineMeetingRepository.findById(meeting.getId()).get();
		String savedImage = savedMeeting.getBackgroundImage().getImageUrl();
		// Then
	}
}
