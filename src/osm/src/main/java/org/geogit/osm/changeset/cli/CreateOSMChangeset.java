/* Copyright (c) 2013 OpenPlans. All rights reserved.
 * This code is licensed under the BSD New License, available at the root
 * application directory.
 */

package org.geogit.osm.changeset.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import org.geogit.api.GeoGIT;
import org.geogit.cli.AbstractCommand;
import org.geogit.cli.CLICommand;
import org.geogit.cli.GeogitCLI;
import org.geogit.osm.changeset.internal.CreateOSMChangesetOp;
import org.openstreetmap.osmosis.core.container.v0_6.ChangeContainer;
import org.openstreetmap.osmosis.xml.v0_6.XmlChangeWriter;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Imports data from OSM using the Overpass API
 */
@Parameters(commandNames = "create-changeset", commandDescription = "Save diff between versions as OSM changeset")
public class CreateOSMChangeset extends AbstractCommand implements CLICommand {

    @Parameter(description = "[<commit> [<commit>]]", arity = 2)
    private List<String> refSpec = Lists.newArrayList();

    @Parameter(names = "-f", description = "File to save changesets to")
    private String file;

    /**
     * Executes the command with the specified options.
     * 
     * @param cli
     * @throws Exception
     * @see org.geogit.cli.AbstractCommand#runInternal(org.geogit.cli.GeogitCLI)
     */
    @Override
    protected void runInternal(GeogitCLI cli) throws Exception {

        Preconditions.checkArgument(refSpec.size() < 3, "Commit list is too long :" + refSpec);

        GeoGIT geogit = cli.getGeogit();

        CreateOSMChangesetOp op = geogit.command(CreateOSMChangesetOp.class);

        String oldVersion = resolveOldVersion();
        String newVersion = resolveNewVersion();

        op.setOldVersion(oldVersion).setNewVersion(newVersion);

        Iterator<ChangeContainer> entries;
        entries = op.setProgressListener(cli.getProgressListener()).call();

        if (!entries.hasNext()) {
            cli.getConsole().println("No differences found");
            return;
        }
        BufferedWriter bufWriter = new BufferedWriter(new FileWriter(new File(file)));
        XmlChangeWriter writer = new XmlChangeWriter(bufWriter);
        while (entries.hasNext()) {
            ChangeContainer change = entries.next();
            writer.process(change);
        }
        writer.complete();
        bufWriter.flush();

    }

    private String resolveOldVersion() {
        return refSpec.size() > 0 ? refSpec.get(0) : null;
    }

    private String resolveNewVersion() {
        return refSpec.size() > 1 ? refSpec.get(1) : null;
    }

}
