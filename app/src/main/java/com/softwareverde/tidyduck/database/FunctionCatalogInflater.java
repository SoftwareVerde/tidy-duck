package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.logging.Logger;
import com.softwareverde.logging.slf4j.Slf4jLogger;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.most.*;
import com.softwareverde.util.Util;

import java.sql.Connection;
import java.util.*;

public class FunctionCatalogInflater {
    protected final Logger _logger = new Slf4jLogger(this.getClass());
    protected final DatabaseConnection<Connection> _databaseConnection;

    public FunctionCatalogInflater(final DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    /**
     * <p>Inflates function catalogs without child objects.</p>
     * @return
     * @throws DatabaseException
     */
    public List<FunctionCatalog> inflateFunctionCatalogs() throws DatabaseException {
        return inflateFunctionCatalogs(false);
    }

    public List<FunctionCatalog> inflateFunctionCatalogs(final boolean inflateChildren) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM function_catalogs ORDER BY base_version_id"
        );

        final ArrayList<FunctionCatalog> functionCatalogs = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            FunctionCatalog functionCatalog = _convertRowToFunctionCatalog(row);

            if (inflateChildren) {
                _inflateChildren(functionCatalog);
                _inflateClassDefinitions(functionCatalog);
                _inflatePropertyCommandDefinitions(functionCatalog);
                _inflateMethodCommandDefinitions(functionCatalog);
                _inflatePropertyReportDefinitions(functionCatalog);
                _inflateMethodReportDefinitions(functionCatalog);
                _inflateTypeDefinitions(functionCatalog);
                _inflateUnitDefinitions(functionCatalog);
                _inflateErrorDefinitions(functionCatalog);
            }
            functionCatalogs.add(functionCatalog);
        }

        return functionCatalogs;
    }

    public Map<Long, List<FunctionCatalog>> inflateFunctionCatalogsMatchingSearchString(final String searchString, final boolean includeDeleted, final Long accountId) throws DatabaseException {
        // Recall that "LIKE" is case-insensitive for MySQL: https://stackoverflow.com/a/14007477/3025921
        final Query query = new Query ("SELECT * FROM function_catalogs\n" +
                "WHERE base_version_id IN (" +
                "SELECT DISTINCT function_catalogs.base_version_id\n" +
                "FROM function_catalogs\n" +
                "WHERE function_catalogs.name LIKE ?" +
                ")\n" +
                "AND (is_approved = ? OR creator_account_id = ? OR creator_account_id IS NULL)\n" +
                (includeDeleted ? "" : "AND is_deleted = 0"));
        query.setParameter("%" + searchString + "%");
        query.setParameter(true);
        query.setParameter(accountId);

        List<FunctionCatalog> functionCatalogs = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            FunctionCatalog functionCatalog = _convertRowToFunctionCatalog(row);
            functionCatalogs.add(functionCatalog);
        }
        return _groupByBaseVersionId(functionCatalogs);
    }

    public List<FunctionCatalog> inflateTrashedFunctionCatalogs(final boolean inflateChildren) throws DatabaseException {
        final Query query = new Query("SELECT * FROM function_catalogs WHERE is_deleted = 1 and is_permanently_deleted = 0");

        final ArrayList<FunctionCatalog> functionCatalogs = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            FunctionCatalog functionCatalog = _convertRowToFunctionCatalog(row);

            if (inflateChildren) {
                _inflateChildren(functionCatalog);
                _inflateClassDefinitions(functionCatalog);
                _inflatePropertyCommandDefinitions(functionCatalog);
                _inflateMethodCommandDefinitions(functionCatalog);
                _inflatePropertyReportDefinitions(functionCatalog);
                _inflateMethodReportDefinitions(functionCatalog);
                _inflateTypeDefinitions(functionCatalog);
                _inflateUnitDefinitions(functionCatalog);
                _inflateErrorDefinitions(functionCatalog);
            }
            functionCatalogs.add(functionCatalog);
        }

        return functionCatalogs;
    }

    public Map<Long, List<FunctionCatalog>> inflateFunctionCatalogsGroupedByBaseVersionId() throws DatabaseException {
        final List<FunctionCatalog> functionCatalogs = inflateFunctionCatalogs(false);
        return _groupByBaseVersionId(functionCatalogs);
    }

    public Map<Long, List<FunctionCatalog>> inflateTrashedFunctionCatalogsGroupedByBaseVersionId() throws DatabaseException {
        final List<FunctionCatalog> functionCatalogs = inflateTrashedFunctionCatalogs(false);
        return _groupByBaseVersionId(functionCatalogs);
    }

    private Map<Long, List<FunctionCatalog>> _groupByBaseVersionId(final List<FunctionCatalog> functionCatalogs) {
        final HashMap<Long, List<FunctionCatalog>> groupedFunctionCatalogs = new HashMap<>();

        for (final FunctionCatalog functionCatalog : functionCatalogs) {
            Long baseVersionId = functionCatalog.getBaseVersionId();
            if (!groupedFunctionCatalogs.containsKey(baseVersionId)) {
                groupedFunctionCatalogs.put(baseVersionId, new ArrayList<FunctionCatalog>());
            }
            groupedFunctionCatalogs.get(baseVersionId).add(functionCatalog);
        }

        return groupedFunctionCatalogs;
    }

    /**
     * <p>Returns the FunctionCatalog for the specified functionCatalogId.</p>
     *  <p>FunctionCatalog's children are NOT inflated.</p>
     *  <p>Returns null if a FunctionCatalog with the functionCatalogId is not found.</p>
     */
    public FunctionCatalog inflateFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        return inflateFunctionCatalog(functionCatalogId, false);
    }

    /**
     * <p>Returns the FunctionCatalog for the specified functionCatalogId.</p>
     *  <p>If inflateChildren is true, the FunctionCatalog and all of its children are inflated.</p>
     *  <p>Returns null if a FunctionCatalog with the functionCatalogId is not found.</p>
     */
    public FunctionCatalog inflateFunctionCatalog(final long functionCatalogId, final boolean inflateChildren) throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM function_catalogs WHERE id = ?"
        );
        query.setParameter(functionCatalogId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            _logger.warn("Could not find functionCatalog w/ ID: "+ functionCatalogId);
            return null;
        }

        // get first (should be only) row
        final Row row = rows.get(0);
        FunctionCatalog functionCatalog = _convertRowToFunctionCatalog(row);

        if (inflateChildren) {
            _inflateChildren(functionCatalog);
            _inflateClassDefinitions(functionCatalog);
            _inflatePropertyCommandDefinitions(functionCatalog);
            _inflateMethodCommandDefinitions(functionCatalog);
            _inflatePropertyReportDefinitions(functionCatalog);
            _inflateMethodReportDefinitions(functionCatalog);
            _inflateTypeDefinitions(functionCatalog);
            _inflateUnitDefinitions(functionCatalog);
            _inflateErrorDefinitions(functionCatalog);
        }

        return functionCatalog;
    }

    private FunctionCatalog _convertRowToFunctionCatalog(final Row row) throws DatabaseException {
        final Long id = Util.parseLong(row.getString("id"));
        final String name = row.getString("name");
        final String release = row.getString("release_version");
        final Long accountId = row.getLong("account_id");
        final Long companyId = row.getLong("company_id");
        final boolean isDeleted = row.getBoolean("is_deleted");
        final String deletedDateString = row.getString("deleted_date");
        Date deletedDate = null;
        if (deletedDateString != null) {
            deletedDate = DateUtil.dateFromDateTimeString(deletedDateString);
        }
        final boolean isPermanentlyDeleted = row.getBoolean("is_permanently_deleted");
        final String permanentlyDeletedDateString = row.getString("permanently_deleted_date");
        Date permanentlyDeletedDate = null;
        if (permanentlyDeletedDateString != null) {
            permanentlyDeletedDate = DateUtil.dateFromDateTimeString(permanentlyDeletedDateString);
        }
        final boolean isApproved = row.getBoolean("is_approved");
        final Long approvalReviewId = row.getLong("approval_review_id");
        final boolean isReleased = row.getBoolean("is_released");
        final Long baseVersionId = row.getLong("base_version_id");
        final Long priorVersionId = row.getLong("prior_version_id");
        final Long creatorAccountId = row.getLong("creator_account_id");

        final AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(accountId);

        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(companyId);

        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setId(id);
        functionCatalog.setName(name);
        functionCatalog.setRelease(release);
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);
        functionCatalog.setIsDeleted(isDeleted);
        functionCatalog.setDeletedDate(deletedDate);
        functionCatalog.setIsPermanentlyDeleted(isPermanentlyDeleted);
        functionCatalog.setPermanentlyDeletedDate(permanentlyDeletedDate);
        functionCatalog.setIsApproved(isApproved);
        functionCatalog.setApprovalReviewId(approvalReviewId);
        functionCatalog.setIsReleased(isReleased);
        functionCatalog.setBaseVersionId(baseVersionId);
        functionCatalog.setPriorVersionId(priorVersionId);
        functionCatalog.setCreatorAccountId(creatorAccountId);

        return functionCatalog;
    }

    private void _inflateChildren(final FunctionCatalog functionCatalog) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalog.getId(), false, true);
        functionCatalog.setFunctionBlocks(functionBlocks);
    }

    private void _inflateClassDefinitions(final FunctionCatalog functionCatalog) throws DatabaseException {
        final ClassDefinitionInflater classDefinitionInflater = new ClassDefinitionInflater(_databaseConnection);
        final List<ClassDefinition> classDefinitions = classDefinitionInflater.inflateClassDefinitions();
        functionCatalog.setClassDefinitions(classDefinitions);
    }

    private void _inflatePropertyCommandDefinitions(final FunctionCatalog functionCatalog) throws DatabaseException {
        final PropertyCommandDefinitionInflater commandDefinitionInflater = new PropertyCommandDefinitionInflater(_databaseConnection);
        final List<PropertyCommandDefinition> commandDefinitions = commandDefinitionInflater.inflateCommandDefinitions();
        functionCatalog.setPropertyCommandDefinitions(commandDefinitions);
    }

    private void _inflateMethodCommandDefinitions(final FunctionCatalog functionCatalog) throws DatabaseException {
        final MethodCommandDefinitionInflater commandDefinitionInflater = new MethodCommandDefinitionInflater(_databaseConnection);
        final List<MethodCommandDefinition> commandDefinitions = commandDefinitionInflater.inflateCommandDefinitions();
        functionCatalog.setMethodCommandDefinitions(commandDefinitions);
    }

    private void _inflatePropertyReportDefinitions(final FunctionCatalog functionCatalog) throws DatabaseException {
        final PropertyReportDefinitionInflater reportDefinitionInflater = new PropertyReportDefinitionInflater(_databaseConnection);
        final List<PropertyReportDefinition> reportDefinitions = reportDefinitionInflater.inflateReportDefinitions();
        functionCatalog.setPropertyReportDefinitions(reportDefinitions);
    }

    private void _inflateMethodReportDefinitions(final FunctionCatalog functionCatalog) throws DatabaseException {
        final MethodReportDefinitionInflater reportDefinitionInflater = new MethodReportDefinitionInflater(_databaseConnection);
        final List<MethodReportDefinition> reportDefinitions = reportDefinitionInflater.inflateReportDefinitions();
        functionCatalog.setMethodReportDefinitions(reportDefinitions);
    }

    private void _inflateTypeDefinitions(final FunctionCatalog functionCatalog) throws DatabaseException {
        final TypeDefinitionInflater typeDefinitionInflater = new TypeDefinitionInflater(_databaseConnection);
        final List<TypeDefinition> typeDefinitions = typeDefinitionInflater.inflateTypeDefinitions();
        functionCatalog.setTypeDefinitions(typeDefinitions);
    }

    private void _inflateUnitDefinitions(final FunctionCatalog functionCatalog) throws DatabaseException {
        final UnitDefinitionInflater unitDefinitionInflater = new UnitDefinitionInflater(_databaseConnection);
        final List<UnitDefinition> unitDefinitions = unitDefinitionInflater.inflateUnitDefinitions();
        functionCatalog.setUnitDefinitions(unitDefinitions);
    }

    private void _inflateErrorDefinitions(final FunctionCatalog functionCatalog) throws DatabaseException {
        final ErrorDefinitionInflater errorDefinitionInflater = new ErrorDefinitionInflater(_databaseConnection);
        final List<ErrorDefinition> errorDefinitions = errorDefinitionInflater.inflateErrorDefinitions();
        functionCatalog.setErrorDefinitions(errorDefinitions);
    }
}
