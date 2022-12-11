package juicy;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import juicy.library.LibrariesReader;
import juicy.library.Library;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static void main(String[] args) {

        File librariesParent = new File(System.getProperty("user.dir"), "libraries");

        if (!librariesParent.exists())
            librariesParent.mkdirs();

        File librariesData = new File(librariesParent, "data");

        if (!librariesData.exists())
            librariesData.mkdirs();

        ImmutableList<Library> libraries = LibrariesReader.readLibraries(librariesData);
        List<File> files = Arrays.asList(Objects.requireNonNull(librariesParent.listFiles()));

        List<File> toDelete = new ArrayList<>(files);

        libraries.forEach(library -> {

            String artifact = library.getArtifact();
            String version = library.getVersion();

            String fileName = artifact + "-" + version + ".jar";

            AtomicBoolean update = new AtomicBoolean(false);

            files.stream()
                    .filter(file -> file.getName().split("-")[0].equals(artifact))
                    .filter(file -> !file.getName().split("-")[1].replace(".jar", "").equals(version))
                    .forEach(file -> update.set(true));

            File file = new File(librariesParent, fileName);

            if (file.exists()) {

                toDelete.remove(file);

                System.out.println("Skip library " + fileName);
                return;

            }

            String path = Joiner.on('/').join(library.getGroup().replace(".", "/"), artifact, version) + "/" + fileName;

            try {

                URL url = new URL("https://repo1.maven.org/maven2/" + path);
                URLConnection urlConnection = url.openConnection();

                DataInputStream inputStream = new DataInputStream(urlConnection.getInputStream());

                byte[] bytes = new byte[urlConnection.getContentLength()];

                for (int i = 0; i < bytes.length; i++)
                    bytes[i] = inputStream.readByte();

                inputStream.close();

                FileOutputStream fileOutputStream = new FileOutputStream(file);

                fileOutputStream.write(bytes);
                fileOutputStream.close();

                toDelete.remove(file);

                System.out.println((update.get() ? "Update" : "Download") + " library " + fileName);

            } catch (Exception exception) {

                exception.printStackTrace();

            }
        });

        toDelete.forEach(file -> {

            file.delete();
            System.out.println("Delete library " + file.getName());

        });
    }
}