package lx.af.net.HttpRequest.ErrorHandler;

import lx.af.net.HttpRequest.DataHull;

/**
 * author: lx
 * date: 16-1-1
 */
public interface IHandler {

    /**
     * handle possible errors contained in DataHull
     * @param datahull the datahull
     */
    void handleError(DataHull datahull);

}
