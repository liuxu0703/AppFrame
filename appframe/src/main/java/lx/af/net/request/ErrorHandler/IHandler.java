package lx.af.net.request.ErrorHandler;

import lx.af.net.request.DataHull;

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
