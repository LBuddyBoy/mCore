package dev.minechase.core.api.tag.model;

import dev.minechase.core.api.api.Documented;
import dev.minechase.core.api.api.IScoped;
import dev.minechase.core.api.api.MultiScope;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class Tag extends Documented implements IScoped {

    private final UUID id;
    private String name, displayName, suffix, materialString;
    private int weight;
    private boolean publicized = true;
    private final List<String> scopes = new ArrayList<>();

    public Tag(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.displayName = this.name;
        this.suffix = "";
        this.materialString = "NAME_TAG";
        this.weight = 1000;
        this.scopes.add("GLOBAL");
    }

    public Tag(Document document) {
        this.id = this.deserializeUUID(document.getString("id"));
        this.name = document.getString("name");
        this.displayName = document.getString("displayName");
        this.suffix = document.getString("suffix");
        this.weight = document.getInteger("weight");
        this.publicized = document.getBoolean("publicized");
        this.materialString = document.getString("materialString");
        this.scopes.addAll(document.getList("scopes", String.class, new ArrayList<>()));
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("id", this.serializeUUID(this.id))
                .append("name", this.name)
                .append("displayName", this.displayName)
                .append("suffix", this.suffix)
                .append("weight", this.weight)
                .append("publicized", this.publicized)
                .append("materialString", this.materialString)
                .append("scopes", this.scopes)
                ;
    }

    public String getPermission() {
        return "core.tag." + this.name;
    }

}
