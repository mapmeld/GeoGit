package org.geogit.web.api;

import java.util.Arrays;

import org.geogit.api.ObjectId;
import org.geogit.web.api.commands.BeginTransaction;
import org.geogit.web.api.commands.BranchWebOp;
import org.geogit.web.api.commands.CheckoutWebOp;
import org.geogit.web.api.commands.Commit;
import org.geogit.web.api.commands.Diff;
import org.geogit.web.api.commands.EndTransaction;
import org.geogit.web.api.commands.FeatureDiffWeb;
import org.geogit.web.api.commands.GetCommitGraph;
import org.geogit.web.api.commands.Log;
import org.geogit.web.api.commands.LsTree;
import org.geogit.web.api.commands.MergeWebOp;
import org.geogit.web.api.commands.RefParseWeb;
import org.geogit.web.api.commands.RemoteWebOp;
import org.geogit.web.api.commands.Status;
import org.geogit.web.api.commands.TagWebOp;
import org.geogit.web.api.commands.UpdateRefWeb;

/**
 * Builds {@link WebAPICommand}s by parsing a given command name and uses a given parameter set to
 * fill out their variables.
 */
public class CommandBuilder {

    /**
     * Builds the {@link WebAPICommand}.
     * 
     * @param commandName the name of the command
     * @param options the parameter set
     * @return the command that was built
     * @throws CommandSpecException
     */
    public static WebAPICommand build(String commandName, ParameterSet options)
            throws CommandSpecException {
        AbstractWebAPICommand command = null;
        if ("status".equalsIgnoreCase(commandName)) {
            command = buildStatus(options);
        } else if ("log".equalsIgnoreCase(commandName)) {
            command = buildLog(options);
        } else if ("commit".equalsIgnoreCase(commandName)) {
            command = buildCommit(options);
        } else if ("ls-tree".equalsIgnoreCase(commandName)) {
            command = buildLsTree(options);
        } else if ("updateref".equalsIgnoreCase(commandName)) {
            command = buildUpdateRef(options);
        } else if ("diff".equalsIgnoreCase(commandName)) {
            command = buildDiff(options);
        } else if ("refparse".equalsIgnoreCase(commandName)) {
            command = buildRefParse(options);
        } else if ("branch".equalsIgnoreCase(commandName)) {
            command = buildBranch(options);
        } else if ("remote".equalsIgnoreCase(commandName)) {
            command = buildRemote(options);
        } else if ("tag".equalsIgnoreCase(commandName)) {
            command = buildTag(options);
        } else if ("featurediff".equalsIgnoreCase(commandName)) {
            command = buildFeatureDiff(options);
        } else if ("getCommitGraph".equalsIgnoreCase(commandName)) {
            command = buildGetCommitGraph(options);
        } else if ("merge".equalsIgnoreCase(commandName)) {
            command = buildMerge(options);
        } else if ("checkout".equalsIgnoreCase(commandName)) {
            command = buildCheckout(options);
        } else if ("beginTransaction".equalsIgnoreCase(commandName)) {
            command = buildBeginTransaction(options);
        } else if ("endTransaction".equalsIgnoreCase(commandName)) {
            command = buildEndTransaction(options);
        } else {
            throw new CommandSpecException("'" + commandName + "' is not a geogit command");
        }

        command.setTransactionId(options.getFirstValue("transactionId", null));

        return command;
    }

    /**
     * Parses a string to an Integer, using a default value if the was not found in the parameter
     * set.
     * 
     * @param form the parameter set
     * @param key the attribute key
     * @param defaultValue the default value
     * @return the parsed integer
     */
    static Integer parseInt(ParameterSet form, String key, Integer defaultValue) {
        String val = form.getFirstValue(key);
        Integer retval = defaultValue;
        if (val != null) {
            try {
                retval = new Integer(val);
            } catch (NumberFormatException nfe) {
                throw new CommandSpecException("Invalid value '" + val + "' specified for option: "
                        + key);
            }
        }
        return retval;
    }

    /**
     * Builds the {@link Status} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static Status buildStatus(ParameterSet options) {
        Status command = new Status();
        command.setLimit(parseInt(options, "limit", 50));
        command.setOffset(parseInt(options, "offset", 0));
        return command;
    }

    /**
     * Builds the {@link Log} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static Log buildLog(ParameterSet options) {
        Log command = new Log();
        command.setLimit(parseInt(options, "limit", 50));
        command.setOffset(parseInt(options, "offset", null));
        command.setPaths(Arrays.asList(options.getValuesArray("path")));
        command.setSince(options.getFirstValue("since"));
        command.setUntil(options.getFirstValue("until"));
        command.setPage(parseInt(options, "page", 0));
        command.setElementsPerPage(parseInt(options, "show", 3000));
        command.setFirstParentOnly(Boolean.valueOf(options
                .getFirstValue("firstParentOnly", "false")));
        return command;
    }

    /**
     * Builds the {@link Commit} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static Commit buildCommit(ParameterSet options) {
        Commit commit = new Commit();
        commit.setAll(Boolean.valueOf(options.getFirstValue("all", "false")));
        commit.setMessage(options.getFirstValue("message", null));
        return commit;
    }

    /**
     * Builds the {@link LsTree} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static LsTree buildLsTree(ParameterSet options) {
        LsTree lsTree = new LsTree();
        lsTree.setIncludeTrees(Boolean.valueOf(options.getFirstValue("showTree", "false")));
        lsTree.setOnlyTrees(Boolean.valueOf(options.getFirstValue("onlyTree", "false")));
        lsTree.setRecursive(Boolean.valueOf(options.getFirstValue("recursive", "false")));
        lsTree.setVerbose(Boolean.valueOf(options.getFirstValue("verbose", "false")));
        lsTree.setRefList(Arrays.asList(options.getValuesArray("path")));
        return lsTree;
    }

    /**
     * Builds the {@link UpdateRefWeb} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static UpdateRefWeb buildUpdateRef(ParameterSet options) {
        UpdateRefWeb command = new UpdateRefWeb();
        command.setName(options.getFirstValue("name", null));
        command.setDelete(Boolean.valueOf(options.getFirstValue("delete", "false")));
        command.setNewValue(options.getFirstValue("newValue", ObjectId.NULL.toString()));
        return command;
    }

    /**
     * Builds the {@link Diff} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static Diff buildDiff(ParameterSet options) {
        Diff command = new Diff();
        command.setOldRefSpec(options.getFirstValue("oldRefSpec", null));
        command.setNewRefSpec(options.getFirstValue("newRefSpec", null));
        command.setPathFilter(options.getFirstValue("pathFilter", null));
        command.setShowGeometryChanges(Boolean.parseBoolean(options.getFirstValue(
                "showGeometryChanges", "false")));
        return command;
    }

    /**
     * Builds the {@link RefParseWeb} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static RefParseWeb buildRefParse(ParameterSet options) {
        RefParseWeb command = new RefParseWeb();
        command.setName(options.getFirstValue("name", null));
        return command;
    }

    /**
     * Builds the {@link BranchWebOp} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static BranchWebOp buildBranch(ParameterSet options) {
        BranchWebOp command = new BranchWebOp();
        command.setList(Boolean.valueOf(options.getFirstValue("list", "false")));
        command.setRemotes(Boolean.valueOf(options.getFirstValue("remotes", "false")));
        return command;
    }

    /**
     * Builds the {@link RemoteWebOp} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static RemoteWebOp buildRemote(ParameterSet options) {
        RemoteWebOp command = new RemoteWebOp();
        command.setList(Boolean.valueOf(options.getFirstValue("list", "false")));
        return command;
    }

    /**
     * Builds the {@link TagWebOp} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static TagWebOp buildTag(ParameterSet options) {
        TagWebOp command = new TagWebOp();
        command.setList(Boolean.valueOf(options.getFirstValue("list", "false")));
        return command;
    }

    /**
     * Builds the {@link FeatureDiffWeb} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static FeatureDiffWeb buildFeatureDiff(ParameterSet options) {
        FeatureDiffWeb command = new FeatureDiffWeb();
        command.setPath(options.getFirstValue("path", null));
        command.setOldCommitId(options.getFirstValue("oldCommitId", ObjectId.NULL.toString()));
        command.setNewCommitId(options.getFirstValue("newCommitId", ObjectId.NULL.toString()));
        command.setAll(Boolean.valueOf(options.getFirstValue("all", "false")));
        return command;
    }

    /**
     * Builds the {@link GetCommitGraph} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static GetCommitGraph buildGetCommitGraph(ParameterSet options) {
        GetCommitGraph command = new GetCommitGraph();
        command.setDepth(parseInt(options, "depth", 0));
        command.setCommitId(options.getFirstValue("commitId", ObjectId.NULL.toString()));
        command.setPage(parseInt(options, "page", 0));
        command.setElementsPerPage(parseInt(options, "show", 3000));
        return command;
    }

    /**
     * Builds the {@link BeginTransaction} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static BeginTransaction buildBeginTransaction(ParameterSet options) {
        BeginTransaction command = new BeginTransaction();
        return command;
    }

    /**
     * Builds the {@link EndTransaction} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static EndTransaction buildEndTransaction(ParameterSet options) {
        EndTransaction command = new EndTransaction();
        command.setCancel(Boolean.valueOf(options.getFirstValue("cancel", "false")));
        return command;
    }

    /**
     * Builds the {@link MergeWebOp} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static MergeWebOp buildMerge(ParameterSet options) {
        MergeWebOp command = new MergeWebOp();
        command.setNoCommit(Boolean.valueOf(options.getFirstValue("noCommit", "false")));
        command.setCommit(options.getFirstValue("commit", null));
        return command;
    }

    /**
     * Builds the {@link CheckoutWebOp} command.
     * 
     * @param options the parameter set
     * @return the built command
     */
    static CheckoutWebOp buildCheckout(ParameterSet options) {
        CheckoutWebOp command = new CheckoutWebOp();
        command.setName(options.getFirstValue("branch", null));
        return command;
    }
}
