package de.florian.Downloader;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;

public class Main {

	public static void main(String[] args) throws IOException {
		String line;
		HashSet<String> list = new HashSet<>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("BBCSoundEffects.csv"));
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
			br.close();
		} catch (IOException e1) {
			System.out.println("Test");
		}

		Iterator<String> iterator = list.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			i++;
			String[] splitted = iterator.next().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			String nummer = splitted[0].replaceAll("[^A-Za-z0-9\\s.]", "").replaceAll("\\s+", " ").trim();
			String beschreibung = splitted[1].replaceAll("[^A-Za-z0-9\\s.]", "").replaceAll("\\s+", " ").trim();
			String album = splitted[5].replaceAll("[^A-Za-z0-9\\s.]", "").replaceAll("\\s+", " ").trim();

			System.out.println(i + ": " + "D:\\Test\\" + album + "\\" + nummer.replace(".wav", "") + " - "
					+ beschreibung + ".wav");

			File file = new File("D:\\Test\\" + album + "\\" + nummer.replace(".wav", "") + " - " + beschreibung + ".wav");
			if (!file.exists()) {
				try {
					Path filePath = Paths.get("D:\\Test\\" + album + "\\" + nummer.replace(".wav", "") + " - " + beschreibung + ".wav");
					Files.createDirectories(filePath.getParent());
				} catch (IOException e) {
					e.printStackTrace();
				}

				final URL url = new URL("http://bbcsfx.acropolis.org.uk/assets/" + nummer);
				final URLConnection conn = url.openConnection();
				final InputStream is = new BufferedInputStream(conn.getInputStream());
				final OutputStream os = new BufferedOutputStream(new FileOutputStream("D:\\Test\\" + album + "\\" + nummer.replace(".wav", "") + " - " + beschreibung + ".wav"));
				byte[] chunk = new byte[1024];
				int chunkSize;
				while ((chunkSize = is.read(chunk)) != -1) {
					os.write(chunk, 0, chunkSize);
				}
				os.close();
				is.close();
			}
		}
	}
}
