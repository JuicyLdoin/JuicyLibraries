package juicy.library;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Objects;

@UtilityClass
public class LibrariesReader {

    public static ImmutableList<Library> readLibraries(File folder) {

        if (!folder.isDirectory())
            throw new UnsupportedOperationException();

        ImmutableList.Builder<Library> builder = ImmutableList.builder();

        Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .forEach(file -> {

                    try {

                        builder.add(new Gson().fromJson(new FileReader(file), Library.class));

                    } catch (FileNotFoundException e) {

                        throw new RuntimeException(e);

                    }
                });

        return builder.build();

    }
}