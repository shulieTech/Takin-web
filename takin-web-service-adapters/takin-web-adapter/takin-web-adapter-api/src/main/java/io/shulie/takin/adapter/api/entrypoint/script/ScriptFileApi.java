package io.shulie.takin.adapter.api.entrypoint.script;

import io.shulie.takin.adapter.api.model.request.pressure.ScriptAnnounceRequest;
import io.shulie.takin.adapter.api.model.request.script.ScriptVerifyRequest;

public interface ScriptFileApi {

    void verify(ScriptVerifyRequest request);

    void fileAnnounce(ScriptAnnounceRequest request);
}
