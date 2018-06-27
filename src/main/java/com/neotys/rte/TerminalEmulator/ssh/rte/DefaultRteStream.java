package com.neotys.rte.TerminalEmulator.ssh.rte;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.primitives.Bytes;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

final class DefaultRteStream implements RteStream {
	private final RteSource source;
	private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("RteStream-%d").build());
	private final Future<?> reader;
	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	private final Lock bufferLock = new ReentrantLock();
	private final List<RteStreamListener> listeners = new CopyOnWriteArrayList<>();
	
	private DefaultRteStream(final RteSource source) {
		super();
		this.source = source;
		this.reader = executorService.submit(this::readStreamLoop);
	}
	
	static RteStream of(final RteSource source) {
		return new DefaultRteStream(source);
	}
	
	@Override
	public boolean isAlive() {
		return source.isAlive();
	}
	
	@Override
	public void close() throws IOException {
		reader.cancel(true);
		executorService.shutdown();
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	private void readStreamLoop() {
		while(source.isAlive()) {
			try {
				readStreamUnchecked();
				
				fireListeners();
			} catch (final Throwable t) {
				// FIXME: LOG
			}
		}
	}

	private void readStreamUnchecked() throws Throwable {
		final byte[] read = source.read();
		if (read.length == 0) return;
		
		bufferLock.lock();
		try {
			buffer.write(read);
		} finally {
			bufferLock.unlock();
		}
	}
	
	@Override
	public byte[] bufferClear() {
		bufferLock.lock();
		try {
			final byte[] content = buffer.toByteArray();
			buffer.reset();
			return content;
		} finally {
			bufferLock.unlock();
		}
	}
	
	@Override
	public boolean bufferContains(final byte[]... masks) {
		bufferLock.lock();
		try {
			final byte[] bufferAsBytes = buffer.toByteArray();
			for(final byte[] mask: masks) {
				if (Bytes.indexOf(bufferAsBytes, mask) != -1) {
					return true;
				}
			}
		} finally {
			bufferLock.unlock();
		}
		return false;
	}
	
	@Override
	public byte[] bufferCopy() {
		bufferLock.lock();
		try {
			return buffer.toByteArray();
		} finally {
			bufferLock.unlock();
		}
	}
	
	@Override
	public void addListener(final RteStreamListener listener) {
		this.listeners.add(listener);
	}
	
	
	@Override
	public void removeListener(final RteStreamListener listener) {
		listeners.remove(listener);		
	}
	
	private void fireListeners() {
		final byte[] bufferAsCopy = bufferCopy();
		for(final RteStreamListener listener: listeners) {
			try {
				listener.received(bufferAsCopy);
			} catch (final Throwable t) {
				// FIXME: LOG
			}
		}
	}

}
