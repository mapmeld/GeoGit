/* Copyright (c) 2013 OpenPlans. All rights reserved.
 * This code is licensed under the BSD New License, available at the root
 * application directory.
 */

package org.geogit.osm.in.cli;

import java.io.File;

import jline.UnsupportedTerminal;
import jline.console.ConsoleReader;

import org.geogit.api.Platform;
import org.geogit.api.TestPlatform;
import org.geogit.cli.GeogitCLI;
import org.geogit.osm.in.internal.OSMImportOp;
import org.geogit.osm.map.cli.OSMMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class OSMImportTest extends Assert {

    private GeogitCLI cli;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        ConsoleReader consoleReader = new ConsoleReader(System.in, System.out,
                new UnsupportedTerminal());
        cli = new GeogitCLI(consoleReader);
        File workingDirectory = tempFolder.getRoot();
        Platform platform = new TestPlatform(workingDirectory);
        cli.setPlatform(platform);
        cli.execute("init");
        cli.execute("config", "user.name", "Gabriel Roldan");
        cli.execute("config", "user.email", "groldan@opengeo.org");
        assertTrue(new File(workingDirectory, ".geogit").exists());
    }

    @Test
    public void testImport() throws Exception {
        String filename = OSMImportOp.class.getResource("ways.xml").getFile();
        File file = new File(filename);
        cli.execute("osm", "import", file.getAbsolutePath());
        long unstaged = cli.getGeogit().getRepository().getWorkingTree().countUnstaged("node");
        assertTrue(unstaged > 0);
        unstaged = cli.getGeogit().getRepository().getWorkingTree().countUnstaged("way");
        assertTrue(unstaged > 0);
    }

    @Test
    public void testImportWithMapping() throws Exception {
        String filename = OSMImportOp.class.getResource("ways.xml").getFile();
        File file = new File(filename);
        String mappingFilename = OSMMap.class.getResource("mapping.json").getFile();
        File mappingFile = new File(mappingFilename);
        cli.execute("osm", "import", file.getAbsolutePath(), "--mapping",
                mappingFile.getAbsolutePath());
        long unstaged = cli.getGeogit().getRepository().getWorkingTree()
                .countUnstaged("onewaystreets");
        assertTrue(unstaged > 0);
    }

}
