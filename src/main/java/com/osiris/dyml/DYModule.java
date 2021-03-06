/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;


import com.osiris.dyml.utils.UtilsDYModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The in-memory representation of a yaml section.
 * Contains information about its keys, values and comments.
 */
@SuppressWarnings("ALL")
public class DYModule {
    private final UtilsDYModule utils = new UtilsDYModule();
    private DreamYaml yaml;
    private List<String> keys;
    private List<DYValue> values;
    private List<DYValue> defaultValues;
    private List<String> comments;
    private List<String> defaultComments;

    private DYModule parentModule = null;
    private List<DYModule> childModules = new ArrayList<>();

    /**
     * See {@link #DYModule(DreamYaml, List, List, List, List)} for details.
     */
    public DYModule(DreamYaml yaml) {
        this(yaml, (String[]) null);
    }

    /**
     * See {@link #DYModule(DreamYaml, List, List, List, List)} for details.
     */
    public DYModule(DreamYaml yaml, String... keys) {
        List<String> list = new ArrayList<>();
        if (keys != null) list.addAll(Arrays.asList(keys));
        init(yaml, list, null, null, null);
    }

    /**
     * Creates a new module.
     * Null values are allowed for creation, but should be replaced with actual values later.
     *
     * @param yaml          this modules yaml file.
     * @param keys          a list containing its keys. Pass over null to create a new list.
     *                      Note that you must add at least one key, otherwise u can't
     *                      save/parse this module.
     * @param defaultValues a list containing its default values. Pass over null to create a new list.
     * @param values        a list containing its values. Pass over null to create a new list.
     * @param comments      a list containing its comments. Pass over null to create a new list.
     */
    public DYModule(DreamYaml yaml, List<String> keys, List<DYValue> defaultValues, List<DYValue> values, List<String> comments) {
        init(yaml, keys, defaultValues, values, comments);
    }

    private void init(DreamYaml yaml, List<String> keys, List<DYValue> defaultValues, List<DYValue> values, List<String> comments) {
        this.yaml = yaml;
        this.keys = keys;
        this.values = values;
        this.defaultValues = defaultValues;
        this.comments = comments;
        if (keys == null) this.keys = new ArrayList<>();
        if (defaultValues == null) this.defaultValues = new ArrayList<>();
        if (values == null) this.values = new ArrayList<>();
        if (comments == null) this.comments = new ArrayList<>();
    }

    /**
     * Prints out this modules most important details.
     */
    public DYModule print() {
        System.out.println(getModuleInformationAsString());
        return this;
    }

    /**
     * Formats this module into a {@link String}.
     */
    public String getModuleInformationAsString() {
        String s = "KEYS: " + this.getKeys().toString() +
                " VALUES: " + utils.valuesListToStringList(this.getValues()).toString() +
                " DEF-VALUES: " + utils.valuesListToStringList(this.getDefValues()).toString() +
                " COMMENTS: " + this.getComments().toString();
        return s;
    }


    // REMOVE METHODS:


    /**
     * Clears the {@link #keys} list.
     */
    public DYModule removeAllKeys() {
        keys.clear();
        return this;
    }

    /**
     * Clears the {@link #values} list.
     */
    public DYModule removeAllValues() {
        values.clear();
        return this;
    }

    /**
     * Clears the {@link #defaultValues} list.
     */
    public DYModule removeAllDefValues() {
        defaultValues.clear();
        return this;
    }

    /**
     * Clears the {@link #comments} list.
     */
    public DYModule removeAllComments() {
        comments.clear();
        return this;
    }


    // ADD METHODS:


    /**
     * Adds a new key to the list. <br>
     * Duplicate keys and null keys are not allowed.
     */
    public DYModule addKeys(String... keys) {
        for (String key :
                keys) {
            Objects.requireNonNull(key);
            this.keys.add(key);
        }
        return this;
    }

    /**
     * See {@link #addValues(List)} for details.
     */
    public DYModule addValues(String... v) {
        addValues(utils.stringArrayToValuesList(v));
        return this;
    }

    /**
     * See {@link #addValues(List)} for details.
     */
    public DYModule addValues(DYValue... v) {
        addValues(Arrays.asList(v));
        return this;
    }

    /**
     * Adds new values to the list. <br>
     * Checks for duplicate keys, if the value is a {@link DYModule}.
     */
    public DYModule addValues(List<DYValue> v) {
        Objects.requireNonNull(v);
        for (DYValue value :
                v) {
            Objects.requireNonNull(value);
        }
        this.values.addAll(v);
        return this;
    }

    /**
     * Converts the provided string array, into a {@link DYValue}s list. <br>
     * See {@link #addDefValues(List)} for details.
     */
    public DYModule addDefValues(String... v) {
        if (v != null)
            addDefValues(utils.stringArrayToValuesList(v));
        return this;
    }

    /**
     * {@link #addDefValues(List)}
     */
    public DYModule addDefValues(DYValue... v) {
        if (v != null)
            addDefValues(Arrays.asList(v));
        return this;
    }

    /**
     * Adds new default {@link DYValue}s to the list. <br>
     * Note that the list cannot contain null {@link DYValue}s. <br>
     * {@link DYValue#asString()} may return null though.
     */
    public DYModule addDefValues(List<DYValue> v) {
        Objects.requireNonNull(v);
        for (DYValue value :
                v) {
            Objects.requireNonNull(value);
        }
        defaultValues.addAll(v);
        return this;
    }

    public DYModule addComments(String... c) {
        if (c != null)
            this.comments.addAll(Arrays.asList(c));
        return this;
    }

    public DYModule addDefComments(String... c) {
        if (c != null)
            this.defaultComments.addAll(Arrays.asList(c));
        return this;
    }


    // SET METHODS:

    public UtilsDYModule getUtils() {
        return utils;
    }

    /**
     * Returns the first key located at index 0.
     */
    public String getFirstKey() {
        return getKeyByIndex(0);
    }

    /**
     * Returns the last key.
     */
    public String getLastKey() {
        return getKeyByIndex(keys.size() - 1);
    }

    /**
     * Returns the key by given index or
     * null if there was no index i in the list.
     */
    public String getKeyByIndex(int i) {
        try {
            return keys.get(i);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Returns all keys. Their order is essential for a correct yaml file. <br>
     */
    public List<String> getKeys() {
        return keys;
    }

    /**
     * See {@link #setKeys(List)} for details.
     */
    public DYModule setKeys(String... keys) {
        if (keys != null) return setKeys(Arrays.asList(keys));
        return this;
    }

    /**
     * Clears the list and adds the given keys.
     * Duplicate keys are not allowed,
     * because its the only way of distinguishing modules.
     */
    public DYModule setKeys(List<String> keys) {
        if (keys != null) {
            this.keys.clear();
            this.keys.addAll(keys);
        }
        return this;
    }

    /**
     * Returns the 'real' value from the yaml file
     * at the time when load() was called.
     */
    public DYValue getValue() {
        return getValueByIndex(0);
    }

    /**
     * Returns the value by given index or
     * its default value, if the value is null/empty and {@link DreamYaml#isReturnDefaultWhenValueIsNullEnabled()} is set to true.
     */
    public DYValue getValueByIndex(int i) {
        DYValue v = new DYValue((String) null);
        try {
            v = values.get(i);
        } catch (Exception ignored) {
        }

        if (v.asString() == null && yaml.isReturnDefaultWhenValueIsNullEnabled())
            return getDefValueByIndex(i);
        return v;
    }

    public List<DYValue> getValues() {
        return values;
    }

    /**
     * See {@link #setValues(List)} for details.
     */
    public DYModule setValues(String... v) {
        setValues(utils.stringArrayToValuesList(v));
        return this;
    }

    /**
     * Not allowed to contain null {@link DYValue}s. <br>
     * See {@link #setValues(List)} for details.
     */
    public DYModule setValues(DYValue... v) {
        setValues(Arrays.asList(v));
        return this;
    }

    /**
     * Clears the values list and adds the values from the provided list. <br>
     * Note that the list can NOT contain null {@link DYValue}s. <br>
     * {@link DYValue#asString()} may return null though. <br>
     * If you want to remove values, use {@link #removeAllValues()} instead.
     */
    public DYModule setValues(List<DYValue> v) {
        this.values.clear();
        addValues(v);
        return this;
    }

    /**
     * Returns the first {@link DYValue} in the default values list.
     */
    public DYValue getDefValue() {
        return getDefValueByIndex(0);
    }

    /**
     * Returns the {@link DYValue} at index i in the default values list.
     */
    public DYValue getDefValueByIndex(int i) {
        DYValue v = new DYValue((String) null);
        try {
            v = defaultValues.get(i);
        } catch (Exception ignored) {
        }
        return v;
    }

    public List<DYValue> getDefValues() {
        return defaultValues;
    }

    /**
     * See {@link #setDefValues(List)} for details.
     */
    public DYModule setDefValues(String... v) {
        setDefValues(utils.stringArrayToValuesList(v));
        return this;
    }

    /**
     * See {@link #setDefValues(List)} for details.
     */
    public DYModule setDefValues(DYValue... v) {
        setDefValues(Arrays.asList(v));
        return this;
    }

    /**
     * The default values are written to the yaml file, when there were no regular values set/added. <br>
     * Further details: <br>
     * {@link DreamYaml#isWriteDefaultValuesWhenEmptyEnabled()} <br>
     * {@link DreamYaml#isReturnDefaultWhenValueIsNullEnabled()} <br>
     */
    public DYModule setDefValues(List<DYValue> v) {
        this.defaultValues.clear();
        addDefValues(v);
        return this;
    }

    /**
     * Returns the first comment at index 0.
     */
    public String getComment() {
        return getCommentByIndex(0);
    }

    /**
     * Returns a specific comment by its index or null if nothing found at that index.
     */
    public String getCommentByIndex(int i) {
        try {
            return comments.get(i);
        } catch (Exception ignored) {
        }
        return null;
    }

    public List<String> getComments() {
        return comments;
    }

    public DYModule setComments(String... c) {
        if (c != null) setComments(Arrays.asList(c));
        return this;
    }

    public DYModule setComments(List<String> c) {
        if (c != null) {
            this.comments.clear();
            this.comments.addAll(c);
        }
        return this;
    }

    /**
     * Returns the first default comment at index 0.
     */
    public String getDefComment() {
        return getDefCommentByIndex(0);
    }

    /**
     * Returns a specific default comment by its index or null if nothing found at that index.
     */
    public String getDefCommentByIndex(int i) {
        try {
            return defaultComments.get(i);
        } catch (Exception ignored) {
        }
        return null;
    }

    public List<String> getDefComments() {
        return defaultComments;
    }

    public DYModule setDefComments(String... c) {
        if (c != null) setDefComments(Arrays.asList(c));
        return this;
    }

    public DYModule setDefComments(List<String> c) {
        if (c != null) {
            this.defaultComments.clear();
            this.defaultComments.addAll(c);
        }
        return this;
    }


    // AS METHODS:

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as string.
     */
    public String asString() {
        return asString(0);
    }

    public String asString(int i) {
        return getValueByIndex(i).asString();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue}.
     */
    public DYValue asDYValue() {
        return asDYValue(0);
    }

    public DYValue asDYValue(int i) {
        return getValueByIndex(i);
    }

    /**
     * Note that this is a copy and not the original list.
     */
    public List<String> asStringList() {
        return utils.valuesListToStringList(this.values);
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as char-array.
     */
    public char[] asCharArray() {
        return asCharArray(0);
    }

    public char[] asCharArray(int i) {
        return getValueByIndex(i).asCharArray();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as boolean.
     */
    public boolean asBoolean() {
        return asBoolean(0);
    }

    public boolean asBoolean(int i) {
        return getValueByIndex(i).asBoolean();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as byte.
     */
    public byte asByte() {
        return asByte(0);
    }

    public byte asByte(int i) {
        return getValueByIndex(i).asByte();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as short.
     */
    public short asShort() {
        return asShort(0);
    }

    public short asShort(int i) {
        return getValueByIndex(i).asShort();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as int.
     */
    public int asInt() {
        return asInt(0);
    }

    public int asInt(int i) {
        return getValueByIndex(i).asInt();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as long.
     */
    public long asLong() {
        return asLong(0);
    }

    public long asLong(int i) {
        return getValueByIndex(i).asLong();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as float.
     */
    public float asFloat() {
        return asFloat(0);
    }

    public float asFloat(int i) {
        return getValueByIndex(i).asFloat();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as double.
     */
    public Double asDouble() {
        return asDouble(0);
    }

    public Double asDouble(int i) {
        return getValueByIndex(i).asDouble();
    }


    // OTHER METHODS:


    /**
     * <p style="color:red;">Do not modify this directly, unless you know what you are doing!</p>
     * The parent {@link DYModule} of this {@link DYModule}, aka the last {@link DYModule} in the generation before. <br>
     * More about generations here: {@link DYReader#parseLine(DreamYaml, DYLine)}.
     */
    public DYModule getParentModule() {
        return parentModule;
    }

    /**
     * <p style="color:red;">Do not modify this directly, unless you know what you are doing!</p>
     * The parent {@link DYModule} of this {@link DYModule}, aka the last {@link DYModule} in the generation before. <br>
     * More about generations here: {@link DYReader#parseLine(DreamYaml, DYLine)}.
     */
    public DYModule setParentModule(DYModule parentModule) {
        this.parentModule = parentModule;
        return this;
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * A list containing this modules child modules, aka the next generation. <br>
     * Note that this list does NOT contain generations beyond that. <br>
     * More about generations here: {@link DYReader#parseLine(DreamYaml, DYLine)}.
     */
    public List<DYModule> getChildModules() {
        return childModules;
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * A list containing this modules child modules, aka the next generation. <br>
     * Note that this list does NOT contain generations beyond that. <br>
     * More about generations here: {@link DYReader#parseLine(DreamYaml, DYLine)}.
     */
    public DYModule setChildModules(List<DYModule> childModules) {
        this.childModules = childModules;
        return this;
    }

    public DYModule addChildModules(DYModule... cModules) {
        Objects.requireNonNull(cModules);
        childModules.addAll(Arrays.asList(cModules));
        return this;
    }


}
