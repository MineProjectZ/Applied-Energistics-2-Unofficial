package appeng.api.networking.events;

import appeng.api.networking.request.IRequestProvider;

public class MENetworkRequestProviderChange extends MENetworkEvent {
    
    public IRequestProvider provider;

    public MENetworkRequestProviderChange(IRequestProvider provider) {
        this.provider = provider;
    }

}
