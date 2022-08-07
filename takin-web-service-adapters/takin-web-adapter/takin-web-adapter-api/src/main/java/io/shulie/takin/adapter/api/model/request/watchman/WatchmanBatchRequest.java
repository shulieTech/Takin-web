package io.shulie.takin.adapter.api.model.request.watchman;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class WatchmanBatchRequest extends ContextExt {

    private List<String> watchmanIdList;
}
