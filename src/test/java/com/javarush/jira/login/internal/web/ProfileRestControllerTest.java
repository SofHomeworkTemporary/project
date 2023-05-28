package com.javarush.jira.login.internal.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.common.util.validation.ValidationUtil;
import com.javarush.jira.profile.ContactTo;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.Profile;
import com.javarush.jira.profile.internal.ProfileMapper;
import com.javarush.jira.profile.internal.ProfileRepository;
import com.javarush.jira.profile.internal.ProfileUtil;
import com.javarush.jira.profile.web.ProfileRestController;
import org.aspectj.lang.annotation.Before;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static com.javarush.jira.login.internal.web.RegisterController.REGISTER_URL;
import static com.javarush.jira.login.internal.web.UserTestData.USER_MAIL;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileRestControllerTest extends AbstractControllerTest {

    @MockBean
    protected ProfileMapper profileMapper;
    @MockBean
    private ProfileRepository profileRepository;
    @Autowired
    MockMvc mockMvc;
    static ProfileTo profileTo;
    static Profile profile;
    static ProfileTo profileToUpdate;

    @BeforeAll
    public static void init() {
        profileTo = new ProfileTo(1L, Set.of("String", "String2"), Set.of(new ContactTo("String", "String"),
                new ContactTo("String1", "String1")));
        profile = new Profile(1L);
        profileToUpdate = new ProfileTo(1L, Set.of("Updated", "Updated2"), Set.of(new ContactTo("mobile", "+01234567890"),
                new ContactTo("website", "user.com")));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    public void getIntegrationTest() throws Exception {
        when(profileRepository.getOrCreate(profileTo.getId()))
                .thenReturn(profile);
        when(profileMapper.toTo(profile)).thenReturn(profileTo);
        mockMvc.perform(MockMvcRequestBuilders.get(ProfileRestController.REST_URL)).
                andDo(print())
                .andExpect(status().isOk());
        verify(profileRepository, only()).getOrCreate(profileTo.getId());
    }

    @Test
    @WithUserDetails(USER_MAIL)
    @WithMockUser("testuser1")
    public void updateIntegrationTest() throws Exception {
        String jsonString = new ObjectMapper().writeValueAsString(profileToUpdate);
        Profile profile1 = new Profile(1L);
        when(profileRepository.getOrCreate(profileTo.getId()))
                .thenReturn(profile);
        when(profileMapper.updateFromTo(profile, profileToUpdate)).thenReturn(profile1);
        when(profileRepository.save(profile1)).thenReturn(profile1);
        mockMvc.perform(MockMvcRequestBuilders.put(ProfileRestController.REST_URL).contentType(APPLICATION_JSON_VALUE)
                        .content(jsonString))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(profileMapper, only()).updateFromTo(profile, profileToUpdate);
    }


}
