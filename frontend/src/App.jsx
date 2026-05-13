import { useEffect, useMemo, useState } from 'react';

function App() {
  const [vk, setVk] = useState([]);
  const [tg, setTg] = useState([]);
  const [vkCtor, setVkCtor] = useState('full');
  const [tgCtor, setTgCtor] = useState('token');
  const [editType, setEditType] = useState('vk');
  const [invokeType, setInvokeType] = useState('vk');
  const [parentType, setParentType] = useState('vk');
  const [editId, setEditId] = useState('');
  const [editValue1, setEditValue1] = useState('');
  const [editValue2, setEditValue2] = useState('');

  const [vkVersion, setVkVersion] = useState('5.199');
  const [vkToken, setVkToken] = useState('vk_new_token');
  const [tgToken, setTgToken] = useState('tg_new');
  const [tgChatId, setTgChatId] = useState('100500');

  const [invokeId, setInvokeId] = useState('');
  const [invokeCommand, setInvokeCommand] = useState('getText');
  const [result, setResult] = useState('{}');
  const [error, setError] = useState('');
  const [vkPostApiId, setVkPostApiId] = useState('');
  const [vkPostText, setVkPostText] = useState('');
  const [vkPostLikes, setVkPostLikes] = useState('0');
  const [tgPayloadApiId, setTgPayloadApiId] = useState('');
  const [tgPayloadType, setTgPayloadType] = useState('message');
  const [tgPayloadData, setTgPayloadData] = useState('');
  const [vkPostsByApi, setVkPostsByApi] = useState({});
  const [tgPayloadsByApi, setTgPayloadsByApi] = useState({});

  const API = 'http://localhost:8080/api';

  const loadLists = async () => {
    try {
      setError('');
      const [vkResp, tgResp] = await Promise.all([
        fetch(`${API}/vkapis`),
        fetch(`${API}/tgapis`)
      ]);
      if (!vkResp.ok || !tgResp.ok) throw new Error('Не удалось загрузить списки API');

      const [vkData, tgData] = await Promise.all([vkResp.json(), tgResp.json()]);
      setVk(Array.isArray(vkData) ? vkData : []);
      setTg(Array.isArray(tgData) ? tgData : []);
    } catch (e) {
      setError(e.message || 'Ошибка загрузки');
    }
  };

  useEffect(() => {
    loadLists();
  }, []);

  const ids = useMemo(() => {
    const source = invokeType === 'vk' ? vk : tg;
    return source
      .map((x, index) => {
        if (x?.id === undefined || x?.id === null) return null;
        return String(x.id);
      })
      .filter(Boolean);
  }, [invokeType, vk, tg]);

  useEffect(() => {
    if (invokeType === 'vk') {
      setInvokeCommand('getText');
      setInvokeId(vk.length > 0 ? String(vk[0].id) : '');
    } else {
      setInvokeCommand('sendMessage');
      setInvokeId(tg.length > 0 ? String(tg[0].id) : '');
    }
  }, [invokeType, vk, tg]);

  useEffect(() => {
    if (editType === 'vk') {
      const first = vk.find((x) => x?.id !== undefined && x?.id !== null);
      setEditId(first ? String(first.id) : '');
      setEditValue1(first?.version ?? '5.199');
      setEditValue2(first?.token ?? '');
    } else {
      const first = tg.find((x) => x?.id !== undefined && x?.id !== null);
      setEditId(first ? String(first.id) : '');
      setEditValue1(first?.botToken ?? '');
      setEditValue2(first?.chatId !== undefined && first?.chatId !== null ? String(first.chatId) : '0');
    }
  }, [editType, vk, tg]);

  useEffect(() => {
    const firstVk = vk.find((x) => x?.id !== undefined && x?.id !== null);
    const firstTg = tg.find((x) => x?.id !== undefined && x?.id !== null);
    setVkPostApiId(firstVk ? String(firstVk.id) : '');
    setTgPayloadApiId(firstTg ? String(firstTg.id) : '');
  }, [vk, tg]);

  const addVk = async () => {
    try {
      setError('');
      if (vkCtor === 'full') {
        await fetch(`${API}/vkapis/full`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ version: vkVersion, token: vkToken })
        });
      } else if (vkCtor === 'token') {
        await fetch(`${API}/vkapis/token`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ token: vkToken })
        });
      } else {
        await fetch(`${API}/vkapis/default`, { method: 'POST' });
      }
      await loadLists();
    } catch (e) {
      setError(e.message || 'Ошибка добавления VK');
    }
  };

  const addTg = async () => {
    try {
      setError('');
      if (tgCtor === 'full') {
        await fetch(`${API}/tgapis/full`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ botToken: tgToken, chatId: Number(tgChatId || 0) })
        });
      } else if (tgCtor === 'token') {
        await fetch(`${API}/tgapis/token`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ botToken: tgToken })
        });
      } else {
        await fetch(`${API}/tgapis/chat`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ chatId: Number(tgChatId || 0) })
        });
      }
      await loadLists();
    } catch (e) {
      setError(e.message || 'Ошибка добавления TG');
    }
  };

  const removeVk = async (id) => {
    if (id === undefined || id === null || id === '') {
      setError('Нельзя удалить: у записи отсутствует id');
      return;
    }
    try {
      await fetch(`${API}/vkapis/${id}`, { method: 'DELETE' });
      await loadLists();
    } catch (e) {
      setError(e.message || 'Ошибка удаления VK');
    }
  };

  const removeTg = async (id) => {
    if (id === undefined || id === null || id === '') {
      setError('Нельзя удалить: у записи отсутствует id');
      return;
    }
    try {
      await fetch(`${API}/tgapis/${id}`, { method: 'DELETE' });
      await loadLists();
    } catch (e) {
      setError(e.message || 'Ошибка удаления TG');
    }
  };

  const invokeInitialize = async () => {
    if (!invokeId) return;
    const base = invokeType === 'vk' ? 'vkapis' : 'tgapis';
    const resp = await fetch(`${API}/${base}/${invokeId}/initialize`);
    setResult(await resp.text());
  };

  const invokeFetchData = async () => {
    if (!invokeId) return;
    const base = invokeType === 'vk' ? 'vkapis' : 'tgapis';
    const resp = await fetch(`${API}/${base}/${invokeId}/fetchdata/${invokeCommand}`);
    setResult(await resp.text());
  };

  const invokeRun = async () => {
    if (!invokeId) return;
    const base = invokeType === 'vk' ? 'vkapis' : 'tgapis';
    const resp = await fetch(`${API}/${base}/${invokeId}/run/${invokeCommand}`);
    const data = await resp.json();
    if (typeof data?.result === 'string') setResult(data.result);
    else setResult(JSON.stringify(data?.result ?? data, null, 2));
  };

  const saveEdit = async () => {
    if (!editId) {
      setError('Нет id для редактирования');
      return;
    }
    try {
      setError('');
      if (editType === 'vk') {
        const resp = await fetch(`${API}/vkapis/${editId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ version: editValue1, token: editValue2 })
        });
        if (!resp.ok) throw new Error('Не удалось обновить VK');
      } else {
        const resp = await fetch(`${API}/tgapis/${editId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ botToken: editValue1, chatId: Number(editValue2 || 0) })
        });
        if (!resp.ok) throw new Error('Не удалось обновить TG');
      }
      await loadLists();
    } catch (e) {
      setError(e.message || 'Ошибка редактирования');
    }
  };

  const invokeRunDirect = async (type, id, command) => {
    if (id === undefined || id === null || id === '') return;
    setInvokeType(type);
    setInvokeId(String(id));
    setInvokeCommand(command);
    const base = type === 'vk' ? 'vkapis' : 'tgapis';
    const resp = await fetch(`${API}/${base}/${id}/run/${command}`);
    const data = await resp.json();
    if (typeof data?.result === 'string') setResult(data.result);
    else setResult(JSON.stringify(data?.result ?? data, null, 2));
  };

  const loadRelated = async () => {
    try {
      const vkRequests = vk
        .filter((item) => item?.id !== undefined && item?.id !== null)
        .map((item) => fetch(`${API}/vkapis/${item.id}/posts`).then((r) => r.ok ? r.json() : []));
      const tgRequests = tg
        .filter((item) => item?.id !== undefined && item?.id !== null)
        .map((item) => fetch(`${API}/tgapis/${item.id}/payloads`).then((r) => r.ok ? r.json() : []));

      const vkResponses = await Promise.all(vkRequests);
      const tgResponses = await Promise.all(tgRequests);

      const nextVkPosts = {};
      vk.filter((item) => item?.id !== undefined && item?.id !== null).forEach((item, idx) => {
        nextVkPosts[item.id] = Array.isArray(vkResponses[idx]) ? vkResponses[idx] : [];
      });

      const nextTgPayloads = {};
      tg.filter((item) => item?.id !== undefined && item?.id !== null).forEach((item, idx) => {
        nextTgPayloads[item.id] = Array.isArray(tgResponses[idx]) ? tgResponses[idx] : [];
      });

      setVkPostsByApi(nextVkPosts);
      setTgPayloadsByApi(nextTgPayloads);
    } catch (e) {
      setError(e.message || 'Ошибка загрузки связанных данных');
    }
  };

  useEffect(() => {
    if (vk.length === 0 && tg.length === 0) return;
    loadRelated();
  }, [vk, tg]);

  const addVkPost = async () => {
    if (!vkPostApiId) {
      setError('Выбери VK ID для поста');
      return;
    }
    try {
      setError('');
      const resp = await fetch(`${API}/vkapis/${vkPostApiId}/posts`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text: vkPostText, likes: Number(vkPostLikes || 0) })
      });
      if (!resp.ok) throw new Error('Не удалось добавить VK пост');
      setVkPostText('');
      setVkPostLikes('0');
      await loadRelated();
    } catch (e) {
      setError(e.message || 'Ошибка добавления поста');
    }
  };

  const addTgPayload = async () => {
    if (!tgPayloadApiId) {
      setError('Выбери TG ID для данных');
      return;
    }
    try {
      setError('');
      const resp = await fetch(`${API}/tgapis/${tgPayloadApiId}/payloads`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ payloadType: tgPayloadType, payloadData: tgPayloadData })
      });
      if (!resp.ok) throw new Error('Не удалось добавить TG данные');
      setTgPayloadData('');
      await loadRelated();
    } catch (e) {
      setError(e.message || 'Ошибка добавления данных');
    }
  };

  const removeVkPost = async (postId) => {
    try {
      const resp = await fetch(`${API}/vkapis/posts/${postId}`, { method: 'DELETE' });
      if (!resp.ok) throw new Error('Не удалось удалить пост');
      await loadRelated();
    } catch (e) {
      setError(e.message || 'Ошибка удаления поста');
    }
  };

  const removeTgPayload = async (payloadId) => {
    try {
      const resp = await fetch(`${API}/tgapis/payloads/${payloadId}`, { method: 'DELETE' });
      if (!resp.ok) throw new Error('Не удалось удалить данные');
      await loadRelated();
    } catch (e) {
      setError(e.message || 'Ошибка удаления данных');
    }
  };

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 md:py-10">
      <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight mb-3">Апишечки :3</h1>
      {error && <p className="mb-3 font-semibold text-red-600">{error}</p>}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5 items-stretch">
        <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 h-full">
          <h2 className="font-bold mb-4">VK APIs</h2>
          <div className="grid grid-cols-1 md:grid-cols-12 gap-2 mb-2">
            <select className="rounded-xl border border-slate-300 px-3 py-2 md:col-span-3" value={vkCtor} onChange={e => setVkCtor(e.target.value)}>
              <option value="full">ctor(version,token)</option>
              <option value="token">ctor(token)</option>
              <option value="default">ctor()</option>
            </select>
            {(vkCtor === 'full' || vkCtor === 'default') && (
              <>
                <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-2" value={vkVersion} onChange={e => setVkVersion(e.target.value)} placeholder="Version" />
                <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-3" value={vkToken} onChange={e => setVkToken(e.target.value)} placeholder="Access token" />
              </>
            )}
            {vkCtor === 'token' && (
              <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-5" value={vkToken} onChange={e => setVkToken(e.target.value)} placeholder="Access token" />
            )}
            <button onClick={addVk} className="w-full min-w-[132px] rounded-xl px-4 py-2 font-bold text-white flex items-center justify-center whitespace-nowrap bg-gradient-to-br from-brandViolet to-brandBlue md:col-span-4">Добавить</button>
          </div>
          <ul className="list-disc pl-5 space-y-3">
            {vk.map((item, index) => (
              <li key={`vk-${item.id ?? index}`}>
                <b>#{item?.id ?? '—'}</b> v{item?.version ?? '—'}
                <div className="flex flex-wrap gap-2 mt-2">
                  <button onClick={() => invokeRunDirect('vk', item?.id, 'getText')} className="rounded-xl px-3 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">getText</button>
                  <button onClick={() => invokeRunDirect('vk', item?.id, 'getLike')} className="rounded-xl px-3 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">getLike</button>
                  <button onClick={() => removeVk(item?.id)} disabled={item?.id === undefined || item?.id === null} className="rounded-xl px-3 py-2 font-bold text-slate-900 bg-gradient-to-br from-amber-400 to-brandYellow disabled:opacity-60">Delete</button>
                </div>
              </li>
            ))}
          </ul>
        </section>

        <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 h-full">
          <h2 className="font-bold mb-4">TG APIs</h2>
          <div className="grid grid-cols-1 md:grid-cols-12 gap-2 mb-2">
            <select className="rounded-xl border border-slate-300 px-3 py-2 md:col-span-3" value={tgCtor} onChange={e => setTgCtor(e.target.value)}>
              <option value="full">ctor(token,chatId)</option>
              <option value="token">ctor(token)</option>
              <option value="chat">ctor(chatId)</option>
            </select>
            {tgCtor === 'full' && (
              <>
                <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-2" value={tgToken} onChange={e => setTgToken(e.target.value)} placeholder="Bot token" />
                <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-3" value={tgChatId} onChange={e => setTgChatId(e.target.value)} placeholder="Chat id" />
              </>
            )}
            {tgCtor === 'token' && (
              <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-5" value={tgToken} onChange={e => setTgToken(e.target.value)} placeholder="Bot token" />
            )}
            {tgCtor === 'chat' && (
              <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-5" value={tgChatId} onChange={e => setTgChatId(e.target.value)} placeholder="Chat id" />
            )}
            <button onClick={addTg} className="w-full min-w-[132px] rounded-xl px-4 py-2 font-bold text-white flex items-center justify-center whitespace-nowrap bg-gradient-to-br from-brandViolet to-brandBlue md:col-span-4">Добавить</button>
          </div>
          <ul className="list-disc pl-5 space-y-3">
            {tg.map((item, index) => (
              <li key={`tg-${item.id ?? index}`}>
                <b>#{item?.id ?? '—'}</b> chat {item?.chatId ?? '—'}
                <div className="flex flex-wrap gap-2 mt-2">
                  <button onClick={() => invokeRunDirect('tg', item?.id, 'sendMessage')} className="rounded-xl px-3 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">sendMessage</button>
                  <button onClick={() => invokeRunDirect('tg', item?.id, 'sendPhoto')} className="rounded-xl px-3 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">sendPhoto</button>
                  <button onClick={() => removeTg(item?.id)} disabled={item?.id === undefined || item?.id === null} className="rounded-xl px-3 py-2 font-bold text-slate-900 bg-gradient-to-br from-amber-400 to-brandYellow disabled:opacity-60">Delete</button>
                </div>
              </li>
            ))}
          </ul>
        </section>
      </div>

      <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 mt-5">
        <h2 className="text-2xl font-bold mb-3">Добавление связанных данных</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mb-3">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-2 border border-slate-200 rounded-2xl p-2">
            <div className="font-semibold lg:col-span-12">Добавить VK Post</div>
            <select className="rounded-xl border border-slate-300 px-3 py-2 lg:col-span-3" value={vkPostApiId} onChange={e => setVkPostApiId(e.target.value)}>
              {vk.filter((item) => item?.id !== undefined && item?.id !== null).map((item) => (
                <option key={`vk-post-${item.id}`} value={String(item.id)}>VK ID: {item.id}</option>
              ))}
              {vk.length === 0 && <option value="">Нет VK</option>}
            </select>
            <input className="rounded-xl border border-slate-300 px-3 py-2 lg:col-span-5" value={vkPostText} onChange={e => setVkPostText(e.target.value)} placeholder="Текст поста" />
            <input className="rounded-xl border border-slate-300 px-3 py-2 lg:col-span-1" value={vkPostLikes} onChange={e => setVkPostLikes(e.target.value)} placeholder="0" />
            <button onClick={addVkPost} className="w-full rounded-xl px-4 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue lg:col-span-3">Добавить</button>
          </div>
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-2 border border-slate-200 rounded-2xl p-2">
            <div className="font-semibold lg:col-span-12">Добавить TG Данные</div>
            <select className="rounded-xl border border-slate-300 px-3 py-2 lg:col-span-3" value={tgPayloadApiId} onChange={e => setTgPayloadApiId(e.target.value)}>
              {tg.filter((item) => item?.id !== undefined && item?.id !== null).map((item) => (
                <option key={`tg-payload-${item.id}`} value={String(item.id)}>TG ID: {item.id}</option>
              ))}
              {tg.length === 0 && <option value="">Нет TG</option>}
            </select>
            <select className="rounded-xl border border-slate-300 px-3 py-2 lg:col-span-3" value={tgPayloadType} onChange={e => setTgPayloadType(e.target.value)}>
              <option value="message">message</option>
              <option value="photo">photo</option>
            </select>
            <input className="rounded-xl border border-slate-300 px-3 py-2 lg:col-span-3" value={tgPayloadData} onChange={e => setTgPayloadData(e.target.value)} placeholder="Value" />
            <button onClick={addTgPayload} className="w-full rounded-xl px-4 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue lg:col-span-3">Добавить</button>
          </div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <div>
            <div className="font-semibold mb-2">VK posts</div>
            <ul className="list-disc pl-5 space-y-1">
              {Object.entries(vkPostsByApi).flatMap(([apiId, posts]) => (posts || []).map((post) => (
                <li key={`post-${post.id}`}>
                  vk#{apiId}: {post.text} :{post.likes}{' '}
                  <button onClick={() => removeVkPost(post.id)} className="ml-2 text-xs text-red-500 border border-red-300 rounded-full px-2">Удалить</button>
                </li>
              )))}
            </ul>
          </div>
          <div>
            <div className="font-semibold mb-2">TG данные</div>
            <ul className="list-disc pl-5 space-y-1">
              {Object.entries(tgPayloadsByApi).flatMap(([apiId, payloads]) => (payloads || []).map((payload) => (
                <li key={`payload-${payload.id}`}>
                  tg#{apiId}: {payload.payloadType}: {payload.payloadData}{' '}
                  <button onClick={() => removeTgPayload(payload.id)} className="ml-2 text-xs text-red-500 border border-red-300 rounded-full px-2">Удалить</button>
                </li>
              )))}
            </ul>
          </div>
        </div>
      </section>

      <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 mt-5">
        <h2 className="text-2xl font-bold mb-3">Редактирование по ID</h2>
        <div className="grid grid-cols-1 md:grid-cols-12 gap-2">
          <select className="rounded-xl border border-slate-300 px-3 py-2 md:col-span-2" value={editType} onChange={e => setEditType(e.target.value)}>
            <option value="vk">VK</option>
            <option value="tg">TG</option>
          </select>
          <select className="rounded-xl border border-slate-300 px-3 py-2 md:col-span-2" value={editId} onChange={e => setEditId(e.target.value)}>
            {(editType === 'vk' ? vk : tg)
              .filter((item) => item?.id !== undefined && item?.id !== null)
              .map((item) => (
                <option key={`${editType}-${item.id}`} value={String(item.id)}>
                  ID: {item.id}
                </option>
              ))}
            {((editType === 'vk' ? vk : tg).filter((item) => item?.id !== undefined && item?.id !== null).length === 0) && (
              <option value="">Нет записей</option>
            )}
          </select>
          <input
            className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-3"
            value={editValue1}
            onChange={e => setEditValue1(e.target.value)}
            placeholder={editType === 'vk' ? 'Новая версия' : 'Новый токен'}
          />
          <input
            className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-3"
            value={editValue2}
            onChange={e => setEditValue2(e.target.value)}
            placeholder={editType === 'vk' ? 'Новый токен' : 'Новый chatId'}
          />
          <button onClick={saveEdit} className="rounded-xl px-4 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue md:col-span-2">Сохранить</button>
        </div>
      </section>

      <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 mt-5">
        <h2 className="text-2xl font-bold mb-3">Вызов метода с фронта</h2>
        <div className="flex flex-col md:flex-row gap-2 mb-2">
          <select className="rounded-xl border border-slate-300 px-3 py-2" value={invokeType} onChange={e => setInvokeType(e.target.value)}>
            <option value="vk">VK</option>
            <option value="tg">TG</option>
          </select>
          <select className="rounded-xl border border-slate-300 px-3 py-2" value={invokeId} onChange={e => setInvokeId(e.target.value)}>
            {ids.length === 0 && <option value="">Нет записей</option>}
            {ids.map((id, index) => <option key={`${id}-${index}`} value={id}>ID: {id}</option>)}
          </select>
          <select className="rounded-xl border border-slate-300 px-3 py-2" value={invokeCommand} onChange={e => setInvokeCommand(e.target.value)}>
            {invokeType === 'vk' ? (
              <>
                <option value="getText">getText</option>
                <option value="getLike">getLike</option>
              </>
            ) : (
              <>
                <option value="sendMessage">sendMessage</option>
                <option value="sendPhoto">sendPhoto</option>
              </>
            )}
          </select>
          <button onClick={invokeInitialize} className="rounded-xl px-4 py-2 font-bold text-white bg-gradient-to-br from-brandBlue to-brandViolet">Initialize</button>
          <button onClick={invokeFetchData} className="rounded-xl px-4 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">FetchData</button>
          <button onClick={invokeRun} className="rounded-xl px-4 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">Выполнить</button>
          <button onClick={loadLists} className="rounded-xl px-4 py-2 font-bold text-slate-900 bg-gradient-to-br from-amber-400 to-brandYellow">Обновить списки</button>
        </div>
      </section>

      <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 mt-5">
        <h2 className="text-2xl font-bold mb-3">Результат вызова методов</h2>
        <div className="whitespace-pre-wrap bg-slate-100 rounded-xl p-3">{result}</div>
      </section>
    </div>
  );
}

export default App;
