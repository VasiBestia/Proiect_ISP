// ==== Author: Babencu Cristian ====

package documente;

import documente.controller.DocumentController;
import documente.repository.DocumentDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import static org.junit.jupiter.api.Assertions.*;

public class IncarcareDocumenteTest {

    @InjectMocks
    private DocumentController documentController;

    @Mock
    private DocumentDAO documentDAO;

    @Mock
    private HttpSession session;

    @Mock
    private MultipartFile fisierMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testIncarcareSucces() throws Exception {
        // Simuleaza utilizator logat
        Mockito.when(session.getAttribute("idUtilizator")).thenReturn(1);

        Mockito.when(fisierMock.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("date_test".getBytes()));


        Mockito.when(fisierMock.isEmpty()).thenReturn(false);
        Mockito.when(fisierMock.getOriginalFilename()).thenReturn("test.pdf");

        Mockito.when(documentDAO.adaugaDocument(Mockito.any())).thenReturn(true);

        ResponseEntity<?> raspuns = documentController.adaugaDocumentDinInterfata(
                "Titlu Test", "Proprietar Test", "Identitate", "Institutie",
                "2027-01-01", fisierMock, session);

        assertEquals(HttpStatus.OK, raspuns.getStatusCode(), "Ar trebui să returneze OK pentru date valide");
    }

    @Test
    public void testIncarcareEroareSesiune() {
        // Simuleaza sesiune goala (null)
        Mockito.when(session.getAttribute("idUtilizator")).thenReturn(null);

        ResponseEntity<?> raspuns = documentController.adaugaDocumentDinInterfata(
                "Test", "Prop", "Tip", "Src", null, fisierMock, session);

        assertEquals(HttpStatus.UNAUTHORIZED, raspuns.getStatusCode());
    }

    @Test
    public void testIncarcareEroareDataExpirata() throws Exception {
        Mockito.when(session.getAttribute("idUtilizator")).thenReturn(1);

        // Trimitem o data din trecut: 2020-01-01
        ResponseEntity<?> raspuns = documentController.adaugaDocumentDinInterfata(
                "Test", "Prop", "Identitate", "Src", "2020-01-01", fisierMock, session);

        assertEquals(HttpStatus.BAD_REQUEST, raspuns.getStatusCode(), "Ar trebui să respingă documente expirate");
    }

    @Test
    public void testIncarcareEroareFisierLipsa() throws Exception {
        Mockito.when(session.getAttribute("idUtilizator")).thenReturn(1);
        Mockito.when(fisierMock.isEmpty()).thenReturn(true);


        Mockito.when(fisierMock.getOriginalFilename()).thenReturn("test.pdf");
        Mockito.when(fisierMock.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[0]));


        ResponseEntity<?> raspuns = documentController.adaugaDocumentDinInterfata(
                "Titlu", "Prop", "Tip", "Src", "2027-01-01", fisierMock, session);

        assertNotEquals(HttpStatus.OK.value(), raspuns.getStatusCode().value());
    }
}