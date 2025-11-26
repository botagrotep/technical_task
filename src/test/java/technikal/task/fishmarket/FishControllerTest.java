package technikal.task.fishmarket;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FishControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void testPublicAccess() throws Exception {
                mockMvc.perform(get("/"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("index"));

                mockMvc.perform(get("/fish"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("index"));
        }

        @Test
        void testAdminAccess() throws Exception {
                mockMvc.perform(get("/fish/create").with(user("admin").roles("ADMIN")))
                                .andExpect(status().isOk())
                                .andExpect(view().name("createFish"));
        }

        @Test
        void testUserAccessDenied() throws Exception {
                mockMvc.perform(get("/fish/create").with(user("user").roles("USER")))
                                .andExpect(status().isForbidden());
        }

        @Test
        void testUnauthenticatedRedirect() throws Exception {
                mockMvc.perform(get("/fish/create"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("http://localhost/login"));
        }

        @Test
        void testCreateFishWithMultipleImages() throws Exception {
                MockMultipartFile file1 = new MockMultipartFile("imageFiles", "test1.jpg", "image/jpeg",
                                "test image 1".getBytes());
                MockMultipartFile file2 = new MockMultipartFile("imageFiles", "test2.jpg", "image/jpeg",
                                "test image 2".getBytes());

                mockMvc.perform(multipart("/fish/create")
                                .file(file1)
                                .file(file2)
                                .param("name", "Test Fish")
                                .param("price", "100.0")
                                .with(user("admin").roles("ADMIN"))
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/fish"));
        }
}
