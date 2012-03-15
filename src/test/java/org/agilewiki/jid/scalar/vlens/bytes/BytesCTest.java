package org.agilewiki.jid.scalar.vlens.bytes;

import junit.framework.TestCase;
import org.agilewiki.jactor.Actor;
import org.agilewiki.jactor.JAFuture;
import org.agilewiki.jactor.JAMailboxFactory;
import org.agilewiki.jactor.MailboxFactory;
import org.agilewiki.jactor.bind.Open;
import org.agilewiki.jactor.components.Include;
import org.agilewiki.jactor.components.JCActor;
import org.agilewiki.jid.CopyJID;
import org.agilewiki.jid.GetSerializedLength;
import org.agilewiki.jid.JidFactories;
import org.agilewiki.jid.ResolvePathname;
import org.agilewiki.jid.jidFactory.NewJID;
import org.agilewiki.jid.scalar.SetValue;
import org.agilewiki.jid.scalar.vlens.Clear;
import org.agilewiki.jid.scalar.vlens.jidjid.JidJidC;

public class BytesCTest extends TestCase {
    public void test() {
        MailboxFactory mailboxFactory = JAMailboxFactory.newMailboxFactory(1);
        try {
            JAFuture future = new JAFuture();
            JCActor factory = new JCActor(mailboxFactory.createMailbox());
            (new Include(JidFactories.class)).call(factory);
            Open.req.call(factory);

            NewJID newBytesJid = new NewJID(JidFactories.BYTES_JID_CTYPE);
            Actor bytes1 = newBytesJid.send(future, factory).thisActor();
            Actor bytes2 = (new CopyJID()).send(future, bytes1);
            BytesJidC.setValueReq(new byte[3]).send(future, bytes2);
            Actor bytes3 = (new CopyJID()).send(future, bytes2);

            int sl = GetSerializedLength.req.send(future, bytes1);
            assertEquals(4, sl);
            sl = GetSerializedLength.req.send(future, bytes2);
            assertEquals(7, sl);
            sl = GetSerializedLength.req.send(future, bytes3);
            assertEquals(7, sl);

            assertNull(BytesJidC.getValueReq.send(future, bytes1));
            assertEquals(3, BytesJidC.getValueReq.send(future, bytes2).length);
            assertEquals(3, BytesJidC.getValueReq.send(future, bytes3).length);

            NewJID newJidJid = new NewJID(JidFactories.JID_JID_CTYPE);
            Actor jidJid1 = newJidJid.send(future, factory).thisActor();
            SetValue sjvbs = JidJidC.setValueReq(JidFactories.BYTES_JID_CTYPE);
            sjvbs.send(future, jidJid1);
            Actor rpa = (new ResolvePathname("0")).send(future, jidJid1);
            assertNull(BytesJidC.getValueReq.send(future, rpa));
            assertTrue(BytesJidC.makeValueReq(new byte[0]).send(future, rpa));
            assertFalse(BytesJidC.makeValueReq(new byte[99]).send(future, rpa));
            rpa = (new ResolvePathname("0")).send(future, jidJid1);
            assertEquals(0, BytesJidC.getValueReq.send(future, rpa).length);
            Clear.req.sendEvent(rpa);
            assertNull(BytesJidC.getValueReq.send(future, rpa));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mailboxFactory.close();
        }
    }
}