package com.aitangba.test.thread.sweet;

import java.util.concurrent.Executor;

/**
 * Created by fhf11991 on 2017/5/27.
 */

public class ExecutorDelivery implements Delivery {

    private final Executor mResponsePoster;

    public ExecutorDelivery() {
        mResponsePoster = new Executor() {

            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };
    }

    @Override
    public void postResponse(Request request, String response) {
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response));
    }

    private class ResponseDeliveryRunnable implements Runnable {
        private Request mRequest;
        private String mResponse;

        public ResponseDeliveryRunnable(Request request, String response) {
            mRequest = request;
            mResponse = response;
        }

        @Override
        public void run() {
            mRequest.deliverResponse(mResponse);
            mRequest.onFinish();
        }
    }
}
