package at.htl.common;

import at.htl.common.io.FileUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @timeline .
 * 14.11.2015: MET 001  created test class
 * 14.11.2015: MET 020  test: creating of folders and files
 * 14.11.2015: MET 002  test: delete directory
 * 15.11.2015: MET 005  test: zipping and unzipping
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileUtilsTest {

    private static final String TEMP_PATH = "src/test/resources/temp";

    @Test
    public void t001CreateDirectories() throws Exception {
        FileUtils.delete(TEMP_PATH);
        FileUtils.createDirectory(TEMP_PATH);
        String test1 = TEMP_PATH + "/test1";
        assertTrue(FileUtils.createDirectory(test1));
        String test2 = TEMP_PATH + "/test2";
        assertTrue(FileUtils.createDirectory(test2));
        assertFalse(FileUtils.createDirectory(test2));
        String test3 = TEMP_PATH + "/test3/test";
        assertFalse(FileUtils.createDirectory(test3));
        String test4 = test1 + "/test4";
        assertTrue(FileUtils.createDirectory(test4));
        assertFalse(FileUtils.createDirectory(test4));
        String test5 = test1 + "/test5";
        assertTrue(FileUtils.createDirectory(test5));
        assertFalse(FileUtils.createDirectory(test5));
    }

    @Test
    public void t002CreateFiles() throws Exception {
        String fileName = TEMP_PATH + "/test1/test1.txt";
        assertTrue(FileUtils.createFile(fileName));
        assertFalse(FileUtils.createFile(fileName));
        fileName = TEMP_PATH + "/test1/test2.txt";
        assertTrue(FileUtils.createFile(fileName));
        fileName = TEMP_PATH + "/test1/test4/test3.txt";
        assertTrue(FileUtils.createFile(fileName));
        fileName = TEMP_PATH + "/test1/test4/test4.txt";
        assertTrue(FileUtils.createFile(fileName));
        fileName = TEMP_PATH + "/test1/test4/test5.txt";
        assertTrue(FileUtils.createFile(fileName));
    }

    @Test
    public void t003ZipDirectory() throws Exception {
        String fileName = TEMP_PATH + "/test1";
        String zipFileName = TEMP_PATH + "/test1.zip";
        assertTrue(FileUtils.zip(fileName, zipFileName));
        //assertFalse(FileUtils.zip(fileName, zipFileName));
        //assertTrue(FileUtils.zip(zipFileName, zipFileName));
    }

    @Test
    public void t004UnzipArchive() throws Exception {
        String fileName = TEMP_PATH + "/test1_unzip";
        String zipFileName = TEMP_PATH + "/test1.zip";
        assertTrue(FileUtils.unzip(zipFileName, fileName));
    }

    @Test
    public void t010Delete() throws Exception {
        FileUtils.delete(TEMP_PATH);
        assertFalse(FileUtils.exists(TEMP_PATH));
    }

    @Test
    public void t20ClassName() throws Exception {

    }
}