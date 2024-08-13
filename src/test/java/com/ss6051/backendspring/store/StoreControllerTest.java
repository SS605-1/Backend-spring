package com.ss6051.backendspring.store;

import com.ss6051.backendspring.account.AccountRepository;
import com.ss6051.backendspring.account.AccountService;
import com.ss6051.backendspring.config.TestSecurityConfig;
import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.tool.JwtTokenProvider;
import com.ss6051.backendspring.schedule.common.ScheduleService;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import com.ss6051.backendspring.store.domain.Address;
import com.ss6051.backendspring.store.domain.Store;
import com.ss6051.backendspring.store.dto.RegisterStoreDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(TestSecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private ScheduleService scheduleService;

    private static final Account account = Account.builder()
            .id(123123L)
            .profile_image_url("https://kakao.com/profile_image.jpg")
            .thumbnail_image_url("https://kakao.com/thumbnail_image.jpg")
            .nickname("nickname")
            .build();


    @BeforeEach
    void beforeEach() {
        accountRepository.save(account);
    }

    @Test
    @WithMockUser
    void testRegisterStore_Success() throws Exception {
        RegisterStoreDto dto = new RegisterStoreDto();
        dto.setStore_name("Test Store");
        dto.setStreet_address("123 Main St");
        dto.setLot_number_address("Unit 1");

        assertThat(accountService.findAccount(123123L)).isEqualTo(java.util.Optional.of(account));
        Store newStore = Store.builder()
                .owner(account)
                .name(dto.getStore_name())
                .address(new Address(dto.street_address, dto.lot_number_address))
                .schedule(null)
                .build();
        assertThat(storeService.registerStore(account.getId(), dto)).isEqualTo(newStore);

        Schedule schedule = Schedule.builder()
                .store(newStore)
                .lastModifiedBy(newStore.getOwner())
                .lastModifiedTime(LocalDateTime.now())
                .build();
        assertThat(scheduleService.createSchedule(newStore)).isEqualTo(schedule);

        Store store = Store.builder()
                .owner(account)
                .name(dto.getStore_name())
                .address(new Address(dto.street_address, dto.lot_number_address))
                .schedule(schedule)
                .build();
        assertThat(storeService.setSchedule(newStore, schedule)).isEqualTo(store);


        mockMvc.perform(MockMvcRequestBuilders.post("/store/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"store_name\": \"Test Store\", \"street_address\": \"123 Main St\", \"lot_number_address\": \"Unit 1\" }")) // 적절한 JSON body 설정
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testFindStore_Success() throws Exception {
        Store store = new Store(); // 필요한 값을 설정하세요
        when(storeService.findStore(anyLong())).thenReturn(java.util.Optional.of(store));

        mockMvc.perform(MockMvcRequestBuilders.post("/store/find")
                        .param("storeId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testFindStore_NotFound() throws Exception {
        when(storeService.findStore(anyLong())).thenReturn(java.util.Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/store/find")
                        .param("storeId", "1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testGenerateCode_Success() throws Exception {
        when(JwtTokenProvider.getAccountIdFromSecurity()).thenReturn(1L); // Mock JwtTokenProvider
        when(storeService.generateCode(anyLong(), anyLong())).thenReturn("0a1z9");

        mockMvc.perform(MockMvcRequestBuilders.post("/store/generateCode")
                        .param("storeId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("0a1z9"));
    }

    @Test
    void testGenerateCode_BadRequest() throws Exception {
        when(JwtTokenProvider.getAccountIdFromSecurity()).thenReturn(1L); // Mock JwtTokenProvider
        when(storeService.generateCode(anyLong(), anyLong())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/store/generateCode")
                        .param("storeId", "1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testRegisterEmployee_Success() throws Exception {
        when(JwtTokenProvider.getAccountIdFromSecurity()).thenReturn(1L); // Mock JwtTokenProvider
        Mockito.doNothing().when(storeService).registerEmployee(anyLong(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/store/registerEmployee")
                        .param("code", "someCode"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRegisterEmployee_BadRequest() throws Exception {
        when(JwtTokenProvider.getAccountIdFromSecurity()).thenReturn(1L); // Mock JwtTokenProvider
        Mockito.doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error")).when(storeService).registerEmployee(anyLong(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/store/registerEmployee")
                        .param("code", "someCode"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testSetRole_Success() throws Exception {
        when(JwtTokenProvider.getAccountIdFromSecurity()).thenReturn(1L); // Mock JwtTokenProvider
        Mockito.doNothing().when(storeService).updateRole(anyLong(), anyLong(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/store/setRole")
                        .param("storeId", "1")
                        .param("role", "MANAGER"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testSetRole_BadRequest() throws Exception {
        when(JwtTokenProvider.getAccountIdFromSecurity()).thenReturn(1L); // Mock JwtTokenProvider
        Mockito.doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error")).when(storeService).updateRole(anyLong(), anyLong(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/store/setRole")
                        .param("storeId", "1")
                        .param("role", "MANAGER"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testSetRole_Forbidden() throws Exception {
        when(JwtTokenProvider.getAccountIdFromSecurity()).thenReturn(1L); // Mock JwtTokenProvider
        Mockito.doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Error")).when(storeService).updateRole(anyLong(), anyLong(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/store/setRole")
                        .param("storeId", "1")
                        .param("role", "MANAGER"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}

